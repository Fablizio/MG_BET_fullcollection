package it.smibet.service;

import it.smibet.domain.UserRequest;
import it.smibet.dto.UserRequestDTO;
import it.smibet.mappers.UserRequestMapper;
import it.smibet.repository.UserRequestRepository;
import it.smibet.types.RequestType;
import it.smibet.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;

@Service
public class UserRequestService {

    @Autowired
    UserRequestRepository userRequestRepository;

    @Autowired
    UserRequestMapper userRequestMapper;

    @Autowired
    RestTemplate restTemplate;

    private String getImageDataBase64(String url) {
        byte[] body = restTemplate.exchange(RequestEntity.get(URI.create(url)).build(), byte[].class).getBody();
        return Base64.getEncoder().encodeToString(body);
    }


    public UserRequestDTO createNewRequest(String imageUrl) {

        UserRequest userRequest = new UserRequest();
        userRequest.setRequestType(RequestType.CREATION_WAIT);
        userRequest.setRequestDate(Utility.now());
        userRequest.setPaymentCode(getImageDataBase64(imageUrl));

        return this.userRequestMapper.convertEntityToDTO(this.userRequestRepository.save(userRequest));
    }

    public UserRequestDTO createRinnovoRequest(String imagePath) {
        UserRequest userRequest = new UserRequest();

        userRequest.setRequestType(RequestType.RENEW_WAIT);
        userRequest.setRequestDate(Utility.now());
        userRequest.setPaymentCode(getImageDataBase64(imagePath));

        return this.userRequestMapper.convertEntityToDTO(this.userRequestRepository.save(userRequest));
    }

    public UserRequestDTO createTrialRequest() {
        UserRequest userRequest = new UserRequest();

        userRequest.setRequestType(RequestType.TRIAL_WAIT);
        userRequest.setRequestDate(Utility.now());

        return this.userRequestMapper.convertEntityToDTO(this.userRequestRepository.save(userRequest));
    }

}
