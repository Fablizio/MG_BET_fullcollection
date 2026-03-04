package it.smibet.service;

import it.smibet.domain.Constant;
import it.smibet.dto.UserDTO;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@SuppressWarnings("ALL")
@Service
@CommonsLog
public class MailService {


    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    ConstantService constantService;

    private static final String[] RECIPIENTS = new String[]{
            "giacintotucciarone@gmail.com",
            "gats200@hotmail.it"

    };
    private static final String DEFAULT_SUBJECT = "Una richiesta è stata inviata dal Bot";
    private static final String FROM = "mg-bet@libero.it";

    public void sendEmail(UserDTO userDTO, String request) {
        try {
            log.info("Nuova richiesta ricevuta - Invio email");

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom(FROM);

            String mailText = String.format("Nuovo utente registrato: \n Nickname:%s  \n Username:%s  \n Request:%s \n Codice:%s",
                    userDTO.getNickname(),
                    userDTO.getUsername() != null ? "@" + userDTO.getUsername() : " - ",
                    request,
                    userDTO.getCode() != null ? userDTO.getCode() : " - ");

            mimeMessageHelper.setText(mailText);
            mimeMessageHelper.setTo(RECIPIENTS);
            mimeMessageHelper.setSubject(DEFAULT_SUBJECT);

            javaMailSender.send(mimeMessage);
            log.info("Email inviate con successo");
        } catch (Exception e) {
            log.error("Errore durante l'invio dell' email", e);
        }
    }

}
