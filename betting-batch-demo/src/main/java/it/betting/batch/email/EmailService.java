package it.betting.batch.email;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@CommonsLog
public class EmailService {


    @Autowired
    JavaMailSender javaMailSender;


    public void sendMail(String error) {
        MimeMessageHelper helper = new MimeMessageHelper(javaMailSender.createMimeMessage());

        try {
            helper.setFrom("mgbetbackoffice@gmail.com");
            helper.addTo("marzio.tullio91@gmail.com");
            helper.addTo("giacintotucciarone@gmail.com");
            helper.setSubject("Errore BETTING BATCH");
            helper.setText(getErrorText(error));
            javaMailSender.send(helper.getMimeMessage());
        } catch (Exception e) {
            log.error("Errore durante l'invio dell email --> Exception --> ", e);
        }

    }


    private String getErrorText(String error) {
        return "Si è verificato il seguente errore: \n" +
                error;

    }

}
