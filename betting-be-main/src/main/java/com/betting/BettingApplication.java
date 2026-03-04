package com.betting;

import com.betting.entity.CodeFriend;
import com.betting.repository.FriendCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class BettingApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BettingApplication.class, args);
    }


    @Autowired

    private FriendCodeRepository friendCodeRepository;


    @Override
    public void run(String... args) throws Exception {


    }
}


