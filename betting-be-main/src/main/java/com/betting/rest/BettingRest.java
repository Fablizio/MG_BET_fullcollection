package com.betting.rest;

import com.betting.dto.*;
import com.betting.service.IRaddoppioService;
import com.betting.service.ISiteService;
import com.betting.service.OddService;
import com.betting.util.BettingUtils;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/betting")
@CommonsLog
public class BettingRest {

    @Autowired
    ISiteService siteService;

    @Autowired
    OddService oddService;

    @Autowired
    IRaddoppioService raddoppioService;

    @Autowired
    BettingUtils bettingUtils;


    @GetMapping("/updateSite")
    public ResponseEntity<Void> updateSite(){
        siteService.updateSite();
        return ResponseEntity.ok().build();

    }


    @GetMapping("/site")
    public ResponseEntity<List<Site2DTO>> getListaSite(){
        return ResponseEntity.ok(siteService.getSiteList());
    }

    @GetMapping
    public ResponseEntity<List<OddDTO>> getBet(@RequestParam("idTeam")String idTeam){
        return ResponseEntity.ok(oddService.getOddsByTeam(idTeam));
    }

    @GetMapping("/listaOdds/{idSite}")
    public ResponseEntity<List<OddDTO>> getBetBySite(@PathVariable("idSite") String idSite) {
        return ResponseEntity.ok(oddService.getOddsBySite(idSite));
    }

    @GetMapping("/genera-schedina")
    public ResponseEntity<List<SchedinaDTO>> generaSchedina(){
        return ResponseEntity.ok(oddService.generaSchedina());
    }

    @GetMapping("/getSingleMatch")
    public ResponseEntity<List<SchedinaDTO>> getSingleMatch(){
        return ResponseEntity.ok(raddoppioService.getSingleMatch());
    }
    @GetMapping("/getTwoMatchs")
    public ResponseEntity<List<SchedinaDTO>> getTwoMatchs(){
        return ResponseEntity.ok(raddoppioService.getTwoMatchs());
    }
    @GetMapping("/getDoppiaChance")
    public ResponseEntity<List<SchedinaDTO>> getDoppiaChance(){
        return ResponseEntity.ok(raddoppioService.getDoppiaChance());
    }

    @GetMapping("/raddoppio/history")
    public ResponseEntity<List<RaddoppioDTO>> getRaddoppioHistory(){
        return ResponseEntity.ok(raddoppioService.getHistory());
    }


    @GetMapping(value = "/filter")
    public ResponseEntity<List<OddDTO>> getByFilter(FilterRequestDTO filterRequestDTO){

        return  ResponseEntity.ok(oddService.getByFilter(filterRequestDTO));
    }



    @GetMapping("/today-match")
    public ResponseEntity<List<OddDTO>> getTodayMath(){
        return  ResponseEntity.ok(oddService.getTodayMatch());
    }

    @GetMapping("/today-smellBet")
    public ResponseEntity<List<OddDTO>> getTodaySmellBet(){
        return  ResponseEntity.ok(oddService.getTodaySmellBet());
    }

    @GetMapping("/getLink")
    public ResponseEntity<LinkDTO> getLink(){
        return ResponseEntity.ok(oddService.getLink());
    }

    @GetMapping("/nowDate")
    public ResponseEntity nowDate(){

        StringBuilder ciccio = new StringBuilder();
        ciccio
                .append("\n")
                .append("Date.from(LocalDateTime.now().toInstant(ZoneOffset.of(\"+02:00\")")
                .append(Date.from(LocalDateTime.now().toInstant(ZoneOffset.of("+02:00"))))
                .append("\n")
                .append("Now Date -->")
                .append(new Date())
                .append("\n")
                .append("Date.from(LocalDateTime.now().withHour(0).withMinute(0).toInstant(ZoneOffset.\"+02:00\")) -->")
                .append(Date.from(LocalDateTime.now().withHour(0).withMinute(0).toInstant(ZoneOffset.of("+02:00"))))
                .append("Date oraDieci = Date.from(LocalDateTime.now().withHour(10).withMinute(0).toInstant(ZoneOffset.UTC))")
                .append(Date.from(LocalDateTime.now().withHour(10).withMinute(0).toInstant(ZoneOffset.UTC)));

        return ResponseEntity.ok(ciccio);
    }
}
