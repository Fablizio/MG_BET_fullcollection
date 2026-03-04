package it.smibet.service;

import it.smibet.domain.Constant;
import it.smibet.exception.HttpStatusCodeException;
import it.smibet.repository.ConstantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ConstantService {

    @Autowired
    ConstantRepository constantRepository;

    public Constant findByCode(String code) {
        return this.constantRepository.findByCode(code).map(constant -> {
            constant.setValue(constant.getValue().replace("\r", ""));
            return constant;
        }).orElseThrow(() -> new HttpStatusCodeException(HttpStatus.NOT_FOUND, "Costante inesistente"));
    }

}
