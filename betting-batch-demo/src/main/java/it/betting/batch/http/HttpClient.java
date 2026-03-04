package it.betting.batch.http;

import it.betting.batch.dto.SchedinaDTO;
import it.betting.batch.email.EmailService;
import it.betting.batch.email.StackTraceToString;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@CommonsLog
@Component
public class HttpClient {



    @Value("${url-bot}")
    private String urlBot;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    EmailService emailService;

    public void sendRaddopio(List<SchedinaDTO> raddoppio){
        try {

            log.info("Inizio chiamata al BOT Url --> "+urlBot);
            HttpHeaders headers = new HttpHeaders();

            HttpEntity<List<SchedinaDTO>> entity = new HttpEntity<>(raddoppio, headers);
            restTemplate.exchange(urlBot, HttpMethod.POST, entity,Void.class);

        } catch (Exception e) {
            log.error("Errore durante la chiamata --> ", e);
            emailService.sendMail("Errore durante la chiamata al BOT Exception  URL: "+ urlBot+" --> "+ StackTraceToString.convert(e));
        }
    }


    public Document getPageHtml(String urlSite) {
        HttpURLConnection con;
        Document html = null;

        try {
            URL url = new URL(urlSite);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            if(con.getResponseCode() == 200)
                html = Jsoup.parse(new String(IOUtils.toByteArray(con.getInputStream())));
            else{
                log.error("Codice risposta diverso da 200");
            }
        } catch (Exception e) {
            log.error("Errore nel metodo getPageHtml --> ",e);
        }

        return html;
    }

}
