package it.smibet.service;

import it.smibet.domain.Constant;
import it.smibet.dto.BroadcastMessageDTO;
import it.smibet.dto.message.SendMessageDTO;
import it.smibet.repository.UserRepository;
import it.smibet.utils.Utility;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Configuration
@EnableScheduling
@CommonsLog
public class AsyncService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BotService botService;

    @Autowired
    ConstantService constantService;

    @Scheduled(cron = "0 0 9 */1 * *")
//    Da attivare solo in test
//    @Scheduled(cron = "0 */1 * * * *")
    public void expirationJob() {
        log.info("START EXPIRATION JOB");

        Constant messageSoonExpiration = constantService.findByCode("MESSAGE_SOON_EXPIRATION");
        Constant messageExpired = constantService.findByCode("MESSAGE_EXPIRED");

        LocalDate today = Utility.now();

        this.userRepository.findAll()
                .forEach(user -> {
                    log.info(user);
                    try {
                        if (user.getTelegramSession() == null || user.getExpiration() == null) {
                            return;
                        }

                        LocalDate expirationDate = user.getExpiration();

                        if (expirationDate.isEqual(today.plusDays(5))) {
                            log.info("User " + user.getNickname() + " "
                                    + (user.getUsername() != null ? user.getUsername() : "(username unavailable)") + " "
                                    + user.getCode() + " will expire in 5 days");

                            this.botService.sendBroadcast(
                                    new BroadcastMessageDTO(
                                            String.format(messageSoonExpiration.getValue(), user.getNickname()),
                                            Collections.singletonList(user.getTelegramSession())
                                    )
                            );
                        }

                        if (expirationDate.isEqual(today.minusDays(1))) {
                            log.info("User " + user.getNickname() + " "
                                    + (user.getUsername() != null ? user.getUsername() : "(username unavailable)") + " "
                                    + user.getCode() + " has expired");

                            this.botService.sendBroadcast(
                                    new BroadcastMessageDTO(
                                            String.format(messageExpired.getValue(), user.getNickname()),
                                            Collections.singletonList(user.getTelegramSession())
                                    )
                            );
                        }
                    } catch (Exception e) {
                        log.error("Errore durante l'esecuzione del batch per utente " + user, e);
                    }
                });

        log.info("END EXPIRATION JOB");
    }
}
