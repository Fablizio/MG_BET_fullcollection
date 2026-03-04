package com.betting.rest;

import com.betting.dto.RaddoppioDTO;
import com.betting.service.IRaddoppioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/betting/raddoppio")
public class RaddoppioRest {


    @Autowired
    private IRaddoppioService iRaddoppioService;


    @GetMapping(value = "/params")
    public ResponseEntity<List<RaddoppioDTO>> getAllRaddoppio(@RequestParam(value = "oneMonth",required = false) boolean oneMonth,
                                                              @RequestParam(value = "threeMonths",required = false) boolean threeMonths,
                                                              @RequestParam(value = "sixMonths",required = false) boolean sixMonths) {


        return ResponseEntity.ok(iRaddoppioService.getHistory(oneMonth, threeMonths, sixMonths));


    }


}
