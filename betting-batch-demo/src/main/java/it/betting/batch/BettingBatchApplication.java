package it.betting.batch;

import it.betting.batch.business.BusinessLogic;
import it.betting.batch.html.Html;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class BettingBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(BettingBatchApplication.class, args);
    }

    @Autowired
    BusinessLogic businessLogic;
//
//    @Autowired
//    OddService oddService;
//
//    @Bean
//    CommandLineRunner args() {
//        return args -> oddService.createRaddoppio();
//    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    Html html;

    @Bean
    CommandLineRunner args() {
        return args -> businessLogic.execute();
    }
}
