package com.betting.service;

import com.betting.dto.CreateAlertRequest;
import com.betting.entity.Odd;
import com.betting.entity.QuotaAlert;
import com.betting.entity.User;
import com.betting.repository.OddRepository;
import com.betting.repository.QuotaAlertRepository;
import com.betting.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import static com.betting.security.JwtAuthenticationFilter.CODE;

@Service
public class QuotaAlertService {


    @Autowired
    private QuotaAlertRepository quotaAlertRepository;

    @Autowired
    private OddRepository oddRepository;

    @Autowired
    private UserService userService;

    public void create(CreateAlertRequest createAlertRequest, HttpServletRequest request) {

        String code = request.getHeader(CODE);

        User user = userService.findByCode(code);

        if (user == null) throw new RuntimeException("User not found");

        Odd match = oddRepository.findById(createAlertRequest.getMatchId()).orElseThrow(() -> new RuntimeException("Odd not found"));

        quotaAlertRepository.save(
                QuotaAlert.builder()
                        .matchId(match)
                        .user(user)
                        .esito(createAlertRequest.getEsito())
                        .quotaTarget(createAlertRequest.getQuotaTarget())
                        .conditionType(createAlertRequest.getConditionType())
                        .build()
        );
    }

    @Transactional
    public void delete(Long quotaAlertId) {
        quotaAlertRepository.deleteById(quotaAlertId);
    }
}
