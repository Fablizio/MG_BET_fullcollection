package it.smibet.mappers;

import it.smibet.dto.PaymentsDataDTO;
import it.smibet.dto.UserDTO;
import it.smibet.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Component
public class UserMapper {

    private static final String PATTERN = "yyyy-MM-dd";

    @Autowired
    UserRequestMapper userRequestMapper;

    public List<User> convertDTOToEntity(List<UserDTO> userDTO) {
        return userDTO.stream().map(this::convertDTOToEntity).collect(Collectors.toList());
    }

    public List<UserDTO> convertEntityToDTO(List<User> userDTO) {
        return userDTO.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }

    public User convertDTOToEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        try {
            User user = new User();

            user.setCode(userDTO.getCode());
            Optional.ofNullable(userDTO.getExpiration()).ifPresent(date -> user.setExpiration(LocalDate.parse(date, ofPattern(PATTERN))));
            user.setId(userDTO.getId());
            user.setNickname(userDTO.getNickname());
            user.setPayment(userDTO.getPayment());
            user.setToken(userDTO.getToken());
            user.setTelegramSession(userDTO.getTelegramSession());
            user.setLastUserRequest(this.userRequestMapper.convertDTOToEntity(userDTO.getLastUserRequestDTO()));
            user.setTrialUsed(userDTO.getTrialUsed());
            user.setUsername(userDTO.getUsername());

            return user;
        } catch (Exception e) {
            throw e;
        }
    }

    public UserDTO convertEntityToDTO(User user) {
        if (user == null) {
            return null;
        }


        try {
            UserDTO userDTO = new UserDTO();

            userDTO.setCode(user.getCode());
            Optional.ofNullable(user.getExpiration()).ifPresent(date -> userDTO.setExpiration(date.format(ofPattern(PATTERN))));
            userDTO.setId(user.getId());
            userDTO.setNickname(user.getNickname());
            userDTO.setPayment(user.getPayment());
            userDTO.setFriends(user.getFriends().size());
            userDTO.setDiscordUsername(user.getDiscordUsername());
            userDTO.setToken(user.getToken());
            userDTO.setTelegramSession(user.getTelegramSession());
            userDTO.setLastUserRequestDTO(this.userRequestMapper.convertEntityToDTO(user.getLastUserRequest()));
            userDTO.setTrialUsed(user.getTrialUsed());
            userDTO.setUsername(user.getUsername());
            userDTO.setFriendCode(user.getFriendCode());

            return userDTO;
        } catch (Exception e) {
            throw e;
        }
    }

    public UserDTO convertEntityToDTOWithPayments(User user) {
        UserDTO userDTO = convertEntityToDTO(user);
        user.getPayments().forEach(payment -> {
            userDTO.getPaymentsData().add(PaymentsDataDTO.builder()
                    .date(payment.getDate().format(ofPattern(PATTERN)))
                    .base64Image(payment.getBase64Image())
                    .build());
        });
        return userDTO;
    }

}
