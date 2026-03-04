package it.smibet.service;

import it.smibet.domain.StatusDoubling;
import it.smibet.dto.StatusDoublingDTO;
import it.smibet.repository.StatusDoublingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusDoublingService {

    private static final String SINGLE_MATCH = "SINGLE_MATCH";
    private static final String TWO_MATCH = "TWO_MATCHES";
    private static final String DOUBLING_CHANCE_MATCH = "DOUBLING_CHANCE_MATCH";

    @Autowired
    StatusDoublingRepository statusDoublingRepository;


    public StatusDoubling getStatus(){
        return statusDoublingRepository.findAll().stream().findFirst().get();
    }

    public void changeStatus(StatusDoublingDTO statusDoublingDTO){
        StatusDoubling effectiveStatus = statusDoublingRepository.findAll().stream().findFirst().get();

        if(SINGLE_MATCH.equals(statusDoublingDTO.getDoubling())){
            effectiveStatus.setActiveSingleMatch(statusDoublingDTO.isActive());
        }
        if(TWO_MATCH.equals(statusDoublingDTO.getDoubling())){
            effectiveStatus.setActiveTwoMatch(statusDoublingDTO.isActive());
        }
        if(DOUBLING_CHANCE_MATCH.equals(statusDoublingDTO.getDoubling())){
            effectiveStatus.setActiveDoublingChanceMatch(statusDoublingDTO.isActive());
        }

        statusDoublingRepository.save(effectiveStatus);
    }
}
