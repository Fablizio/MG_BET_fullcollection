package com.betting.rest;

import com.betting.dto.OddDTO;
import com.betting.service.OddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
public class OddHistoryRest {

    @Autowired
    OddService oddService;

    @GetMapping("/{idTeam}")
    public ResponseEntity<List<OddDTO>> getHistory(@PathVariable("idTeam") String idTeam) {
        return ResponseEntity.ok(oddService.getHistoryByIdTeam(idTeam));
    }


    @GetMapping("/smell-bet")
    public ResponseEntity<List<OddDTO>> getHistorySmellBet(@RequestParam(value = "date") String date) {
        return ResponseEntity.ok(oddService.getHistorySmellBet(date));
    }

    @GetMapping("/today-match")
    public ResponseEntity<List<OddDTO>> getHistoryTodayMatch(@RequestParam(value = "date") String date) {
        return ResponseEntity.ok(oddService.getHistoryTodayMatch(date));
    }

    @GetMapping("/all-matches")
    public ResponseEntity<List<OddDTO>> getAllHistory() {

        List<OddDTO> result = oddService.findAll();
        return ResponseEntity.ok(result);

    }

}
