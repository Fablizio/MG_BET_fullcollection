package com.betting.rest;

import com.betting.dto.ChangeOfOddResponseDTO;
import com.betting.service.ChangeOfOddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/betting/changeOfOdds")
@CrossOrigin
public class ChangeOfOddRest {


    @Autowired
    private ChangeOfOddService changeOfOddService;


    @GetMapping(value = "/{idOdd}")
    public ResponseEntity<List<ChangeOfOddResponseDTO>> getChangeOfOdd(@PathVariable(value = "idOdd") String idOdd){
        List<ChangeOfOddResponseDTO> result = changeOfOddService.findByOdd(idOdd);
        return ResponseEntity.ok(result);
    }



}
