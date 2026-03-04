package com.betting.http;

import com.betting.dto.BroadcastMessageDTO;
import com.betting.dto.SchedinaDTO;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

@CommonsLog
@Component
public class HttpClient {


    @Value("${url-bot}")
    private String urlBot;

    @Autowired
    private RestTemplate restTemplate;

    public void sendMessage(BroadcastMessageDTO broadcastMessageDTO) {
        try {
            RequestEntity<BroadcastMessageDTO> requestEntity = RequestEntity.post(URI.create(urlBot + "/api/sendBroadcast")).body(broadcastMessageDTO);
            this.restTemplate.exchange(requestEntity, Void.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
