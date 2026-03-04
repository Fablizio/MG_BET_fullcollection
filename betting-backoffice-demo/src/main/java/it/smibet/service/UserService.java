package it.smibet.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.smibet.domain.PaymentsData;
import it.smibet.domain.User;
import it.smibet.domain.UserRequest;
import it.smibet.dto.*;
import it.smibet.dto.message.*;
import it.smibet.exception.HttpStatusCodeException;
import it.smibet.mappers.UserMapper;
import it.smibet.repository.PaymentsDataRepository;
import it.smibet.repository.UserRepository;
import it.smibet.types.RequestType;
import it.smibet.utils.ReferralGenerator;
import it.smibet.utils.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRequestService userRequestService;

    @Autowired
    BotService botService;

    @Autowired
    MailService mailService;

    @Value("${jwtSecret}")
    String secret;

    @Value("${smibet.scadenzamesistandard}")
    Integer scadenzaMesiStandard;

    @Value("${smibet.pagamentostandard}")
    Double pagamentoStandard;

    @Value("${smibet.trialPeriodStandard}")
    Integer trialPeriodStandard;


    public UserListDTO findAll() {
        List<User> users = this.userRepository.findAllByOrderByExpirationAscNicknameAsc();
        Long expirated = users.stream().filter(user -> user.getExpiration().isBefore(Utility.now())).count();
        Long expiring = users.stream().filter(user -> !user.getExpiration().isBefore(Utility.now()) && (!user.getExpiration().isAfter(Utility.now().plus(5, ChronoUnit.DAYS)))).count();
        Long activedTest = users.stream().filter(user -> user.getExpiration().isAfter(Utility.now()) && user.getPayment().equals(0.0)).count();
        Long activedPayed = users.stream().filter(user -> user.getExpiration().isAfter(Utility.now()) && !user.getPayment().equals(0.0)).count();

        return UserListDTO
                .builder()
                .users(this.userMapper.convertEntityToDTO(users))
                .expirated(expirated)
                .expiring(expiring)
                .activedPayed(activedPayed)
                .activedTest(activedTest)
                .build();
    }

    public void save(UserDTO user, boolean generateToken) {
        User userEntity = this.userMapper.convertDTOToEntity(user);
        userEntity.setFriendCode(getFriendCode());
        if (user.getCode() == null || user.getCode().trim().isEmpty()) {
            userEntity.setCode(this.getRandomCode());
        }

        if (generateToken)
            userEntity.setToken(generateToken(userEntity));
        this.userRepository.save(userEntity);
    }

    public void update(UserDTO user) {
        Boolean cleanLastRequest = user.getCleanLastRequest();
        User userUpdates = this.userMapper.convertDTOToEntity(user);
        User userStored = this.userRepository.findById(user.getId()).get();

        userStored.setPayment(userUpdates.getPayment());
        userStored.setExpiration(userUpdates.getExpiration());
        userStored.setCode(userUpdates.getCode());
        userStored.setNickname(userUpdates.getNickname());
        userStored.setToken(generateToken(userStored));


        if (cleanLastRequest != null && cleanLastRequest) {
            if (userStored.getLastUserRequest().getRequestType().equals(RequestType.TRIAL_WAIT))
                this.botService.sendTrialCodeToBot(this.userMapper.convertEntityToDTO(userStored));
            if (userStored.getLastUserRequest().getRequestType().equals(RequestType.CREATION_WAIT))
                this.botService.sendCreationCodeToBot(this.userMapper.convertEntityToDTO(userStored));
            if (userStored.getLastUserRequest().getRequestType().equals(RequestType.RENEW_WAIT))
                this.botService.sendRenewCodeToBot(this.userMapper.convertEntityToDTO(userStored));

            userStored.setLastUserRequest(null);
        }


        this.userRepository.save(userStored);
    }

    public UserDTO findById(Integer id) {
        return this.userMapper.convertEntityToDTO(this.userRepository.findById(id).orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND, "ID inesistente")));
    }

    public UserDTO detail(Integer id) {
        return this.userMapper.convertEntityToDTOWithPayments(this.userRepository.findById(id).orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND, "ID inesistente")));
    }

    public UserDTO findByTelegramSession(String telegramSession) {
        return this.userMapper.convertEntityToDTO(
                this.userRepository.findByTelegramSession(telegramSession)
                        .orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND, "TelegramSession inesistente"))

        );
    }

    public UserDTO findByCode(String code) {
        return this.userMapper.convertEntityToDTO(
                this.userRepository.findByCode(code)
                        .orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND, "TelegramSession inesistente"))

        );
    }

    public DataScadenzaDTO findDataScadenzaByCode(String code, String pattern) {
        User user = this.userRepository.findByCode(code).orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Codice non presente"));
        DataScadenzaDTO dataScadenzaDTO = new DataScadenzaDTO();
        dataScadenzaDTO.setDataDiScadenza(user.getExpiration().format(DateTimeFormatter.ofPattern(pattern)));
        return dataScadenzaDTO;
    }

    public DataScadenzaDTO findDataScadenzaByTelegramSession(String telegramSession, String pattern) {
        User user = this.userRepository.findByTelegramSession(telegramSession).orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Codice non presente"));
        DataScadenzaDTO dataScadenzaDTO = new DataScadenzaDTO();
        dataScadenzaDTO.setDataDiScadenza(user.getExpiration().format(DateTimeFormatter.ofPattern(pattern)));
        return dataScadenzaDTO;
    }


    private void checkActiveSubscription(String telegramSession) {
        this.userRepository.findByTelegramSession(telegramSession)
                .flatMap(user -> Optional.ofNullable(user.getExpiration()))
                .ifPresent(expiration -> {
                    if (expiration.isAfter(Utility.now()) || expiration.isEqual(Utility.now())) {
                        throw new HttpStatusCodeException(HttpStatus.UNPROCESSABLE_ENTITY, "E' già presente un abbonamento attivo");
                    }
                });
    }

    public void createNewRegistrationRequest(RegistrationRequestDTO registrationRequestDTO) throws Exception {
        try {
            checkActiveSubscription(registrationRequestDTO.getTelegramSession());

            UserDTO user = this.userMapper.convertEntityToDTO(this.userRepository.findByTelegramSession(registrationRequestDTO.getTelegramSession()).orElse(new User()));
            user.setTelegramSession(registrationRequestDTO.getTelegramSession());
            user.setNickname(registrationRequestDTO.getNickname());
            user.setLastUserRequestDTO(this.userRequestService.createNewRequest(registrationRequestDTO.getFilePath()));
            user.setUsername(registrationRequestDTO.getUsername());

            this.save(user, false);
            mailService.sendEmail(user, "Richiesta generazione codice");
        } catch (HttpStatusCodeException e) {
            throw e;
        }
    }

    private String getFriendCode() {
        String friendCode;
        int attempts = 0;
        int length = 10;

        do {
            friendCode = ReferralGenerator.generateCode(length);
            attempts++;

            if (attempts > 20) {
                length++;
            }
        } while (userRepository.existsByFriendCode(friendCode));

        return friendCode;
    }

    public TrialCodeResponseDTO createNewTrialRequest(TelegramUserDTO telegramUserDTO) {
        checkActiveSubscription(telegramUserDTO.getTelegramSession());
        try {
            User user = this.userRepository.findByTelegramSession(telegramUserDTO.getTelegramSession()).orElse(new User());

            if (Optional.ofNullable(user.getTrialUsed()).orElse(false))
                throw new HttpStatusCodeException(HttpStatus.UNPROCESSABLE_ENTITY, "Prova già richiesta");

            user.setTelegramSession(telegramUserDTO.getTelegramSession());
            user.setNickname(telegramUserDTO.getNickname());
            user.setUsername(telegramUserDTO.getUsername());
            user.setTrialUsed(true);
            user.setCode(getRandomCode());
            user.setPayment(0.0);

            String friendCode = getFriendCode();

            user.setFriendCode(friendCode);
            user.setExpiration(Utility.now().plusDays(this.trialPeriodStandard));

            UserDTO userDTO = this.userMapper.convertEntityToDTO(user);

            this.save(userDTO, true);
            mailService.sendEmail(userDTO, "Periodo di prova");
            return TrialCodeResponseDTO.builder().code(user.getCode()).expiration(user.getExpiration().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).build();
        } catch (HttpStatusCodeException e) {
            throw e;
        }
    }

    public void createNewRinnovoRequest(RinnovoRequestDTO rinnovoRequestDTO) {
        try {
            UserDTO user = this.userMapper.convertEntityToDTO(this.userRepository.findByTelegramSession(rinnovoRequestDTO.getTelegramSession()).orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND)));
            user.setTelegramSession(rinnovoRequestDTO.getTelegramSession());
            user.setLastUserRequestDTO(this.userRequestService.createRinnovoRequest(rinnovoRequestDTO.getFilePath()));
            this.save(user, false);
            mailService.sendEmail(user, "Richiesta rinnovo");
        } catch (HttpStatusCodeException e) {
            throw e;
        }
    }

    private String generateToken(User user) {
        return Jwts
                .builder()
                .setSubject(user.getCode())
                .setIssuedAt(new Date())
                .setExpiration(new Date(user.getExpiration().plusDays(1).atStartOfDay().atZone(ZoneId.of("Europe/Rome")).toInstant().toEpochMilli()))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public void attivaTrial(Integer userId) {
        User user = this.userRepository.findById(userId).get();
        user.setTrialUsed(true);
        user.setCode(getRandomCode());
        user.setPayment(0.0);
        user.setExpiration(Utility.now().plus(this.trialPeriodStandard, ChronoUnit.DAYS));

        UserDTO userDTO = this.userMapper.convertEntityToDTO(user);
        this.botService.sendTrialCodeToBot(userDTO);
        userDTO.setLastUserRequestDTO(null);
        this.save(userDTO, true);
    }

    public void generaCodice(Integer userId) {
        User user = this.userRepository.findById(userId).get();
        user.setCode(getRandomCode());
        user.setPayment(Optional.ofNullable(user.getPayment()).orElse(0.0) + pagamentoStandard);
        user.setExpiration(Utility.now().plusMonths(scadenzaMesiStandard));

        UserDTO userDTO = this.userMapper.convertEntityToDTO(user);
        this.botService.sendCreationCodeToBot(userDTO);

        userDTO.setLastUserRequestDTO(null);

        this.save(userDTO, true);
    }

    public void rinnovaCodice(Integer userId) {
        User user = this.userRepository.findById(userId).get();
        user.setPayment(user.getPayment() + pagamentoStandard);
        user.setExpiration(Utility.now().plusMonths(scadenzaMesiStandard));

        UserDTO userDTO = this.userMapper.convertEntityToDTO(user);
        this.botService.sendRenewCodeToBot(userDTO);

        userDTO.setLastUserRequestDTO(null);

        this.save(userDTO, true);
    }

    private String getRandomCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuilder code = new StringBuilder();
        IntStream.range(0, 10).forEach((i) -> code.append(uuid.charAt((int) (Math.random() * 31))));

        return code.toString().toUpperCase();
    }


    public Boolean checkIfCodeExists(UserDTO userDTO) {
        return this.userRepository.existsByCode(userDTO.getCode());
    }

    public Boolean checkIfCodeWithOtherIdExists(UserDTO userDTO) {
        return this.userRepository.existsByCodeAndIdNot(userDTO.getCode(), userDTO.getId());
    }

    public void delete(Integer idUser) {
        //TODO verificare
        // this.userRepository.deleteById(idUser);
        log.info("################## Elimazione utente con id {} ##################", idUser);
    }

    public void diniegoRichiesta(Integer userId, String message) {
        Optional<User> byId = this.userRepository.findById(userId);
        User user = byId.orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND));
        DiniegoDTO diniegoDTO = new DiniegoDTO();

        switch (user.getLastUserRequest().getRequestType()) {
            case CREATION_WAIT:
                diniegoDTO.setType(BotService.CREATION_TYPE);
                break;
            case RENEW_WAIT:
                diniegoDTO.setType(BotService.RENEW_TYPE);
                break;
            case TRIAL_WAIT:
                diniegoDTO.setType(BotService.TRIAL_TYPE);
                break;
        }

        diniegoDTO.setChatId(user.getTelegramSession());
        diniegoDTO.setMessage(message);

        this.botService.sendDiniego(diniegoDTO);

        user.setLastUserRequest(null);
        this.userRepository.save(user);
    }

    public void messageBroadcast(BroadcastMessageDTO message) {
        message.setChats(userRepository.getTelegramSession());
        botService.sendBroadcast(message);
    }

    public void sendMessage(Integer userId, String message) {
        UserDTO user = this.findById(userId);
        botService.sendBroadcast(new BroadcastMessageDTO(message, Collections.singletonList(user.getTelegramSession())));
    }

    public UserDTO associaCodice(AssociaCodiceMessageDTO associaCodiceMessageDTO) {
        User userByCode = this.userMapper.convertDTOToEntity(this.findByCode(associaCodiceMessageDTO.getCode()));
        User userByTelegramSession = this.userMapper.convertDTOToEntity(this.findByTelegramSession(associaCodiceMessageDTO.getTelegramSession()));

        if (userByCode.getTelegramSession() != null && userByCode.getTelegramSession().equals(associaCodiceMessageDTO.getTelegramSession())) {
            throw new HttpStatusCodeException(HttpStatus.UNPROCESSABLE_ENTITY, "Codice già associato all'utenza richiesta");
        } else if (userByCode.getTelegramSession() != null && !userByCode.getTelegramSession().equals(associaCodiceMessageDTO.getTelegramSession())) {
            throw new HttpStatusCodeException(HttpStatus.FORBIDDEN, "E' già associato un TelegramSession a questo codice");
        }

        if (userByTelegramSession != null) {
            userByTelegramSession.setTelegramSession(null);
            this.userRepository.save(userByTelegramSession);
        }

        userByCode.setTelegramSession(associaCodiceMessageDTO.getTelegramSession());
        userByCode.setCode(associaCodiceMessageDTO.getCode());
        userByCode.setUsername(associaCodiceMessageDTO.getUsername());
        userByCode.setNickname(associaCodiceMessageDTO.getNickname());

        return this.userMapper.convertEntityToDTO(this.userRepository.save(userByCode));

    }

    public TelegramSessionResponse getActiveTelegramSession() {

        List<String> telegramSessionActive = userRepository.getTelegramSessionActive();
        return TelegramSessionResponse.builder().sessions(telegramSessionActive).build();
    }

    @Autowired
    private PaymentsDataRepository paymentsDataRepository;

    @Transactional
    public void processImage(PaymentsDataDTO data) {
        userRepository.findByTelegramSession(data.getTelegramId())
                .ifPresent(user -> {

                    if (user.getLastUserRequest() == null) {

                        user.setLastUserRequest(
                                UserRequest.builder()
                                        .requestDate(LocalDate.now())
                                        .paymentCode("-")
                                        .build()
                        );
                    }

                    user.getLastUserRequest().setRequestType(RequestType.RENEW_WAIT);

                    paymentsDataRepository.save(
                            PaymentsData.builder()
                                    .base64Image(data.getBase64Image())
                                    .status("DA VERIFICARE")
                                    .date(LocalDate.now())
                                    .user(user)
                                    .build()
                    );

                    userRepository.save(user);
                });
    }


    public void rinnovatoCodice(Integer userId) {
        userRepository.findById(userId).ifPresent(user -> {

            if (user.getLastUserRequest() != null) {
                user.getLastUserRequest().setRequestType(RequestType.NONE);
                LocalDate expiration = user.getExpiration();
                LocalDate now = LocalDate.now();
                if (expiration.isAfter(Utility.now())) {
                    now = expiration;
                }
                user.setExpiration(now.plusMonths(1));
                user.setToken(generateToken(user));
                user.setPayment(20D);
                userRepository.save(user);
                String text = "Ciao!\n" +
                        "Grazie di cuore per la fiducia e per la tua recente sottoscrizione su MGBet!\n" +
                        "Siamo davvero felici di averti con noi e pronti a offrirti supporto ogni volta che ne avrai bisogno.\n";
                try {
                    botService.sendBroadcast(new BroadcastMessageDTO(text, Collections.singletonList(user.getTelegramSession())));
                } catch (Exception e) {

                }
            }

        });
    }
}
