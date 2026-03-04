package com.betting.rest;

import com.betting.dto.CreateAlertRequest;
import com.betting.service.QuotaAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/quotaAlert")
public class QuotaAlertRest {

    @Autowired
    private QuotaAlertService quotaAlertService;


    @PostMapping
    public ResponseEntity<Void> createAlert(@RequestBody CreateAlertRequest createAlertRequest,
                                            @Autowired HttpServletRequest request) {

        quotaAlertService.create(createAlertRequest, request);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{quotaAlertId}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long quotaAlertId) {
        quotaAlertService.delete(quotaAlertId);
        return ResponseEntity.ok().build();
    }

}
