package it.smibet;

import it.smibet.dto.message.TelegramUserDTO;
import it.smibet.repository.UserRepository;
import it.smibet.service.UserService;
import it.smibet.utils.ReferralGenerator;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@Controller
@CommonsLog
public class SmibetApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SmibetApplication.class, args);
    }


    @GetMapping("/")
    public String index() {
        return "redirect:/web";
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }

    @Override
    public void run(String... args) {
        log.info("Betting backoffice started!");
    }
}
