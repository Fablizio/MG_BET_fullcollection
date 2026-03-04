package com.betting.rest;

import com.betting.dto.StatisticsDTO;
import com.betting.service.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/betting/statistics")
public class StatisticsRest {

    @Autowired
    IStatisticsService statisticsService;
    /**
     * Restituisce la percentuale di vincita totale
     */
    @GetMapping("/total")
    public ResponseEntity<StatisticsDTO> getTotalStatistics(){
       return ResponseEntity.ok(statisticsService.getTotalStatistics());
    }
    /**
     * Restituisce la percentuale di vincita totale specificando il campionato
     */
    @GetMapping("/campionato/{id}")
    public ResponseEntity<List<StatisticsDTO>> getTotalStatisticsByCampionato(@PathVariable("id") String id){
        return ResponseEntity.ok(statisticsService.getTotalStatisticsByCampionato(id));
    }
    /**
     * Restituisce la percentuale di vincita totale specificando il campionato
     */
    @GetMapping("/total/campionato/{id}")
    public ResponseEntity<StatisticsDTO> getTotalPieChartStatisticsByCampionato(@PathVariable("id") String id){
        return ResponseEntity.ok(statisticsService.getTotalPieChartStatisticsByCampionato(id));
    }

    @GetMapping(value = "/raddoppio")
    public ResponseEntity<StatisticsDTO> getTotalStatisticsRaddoppio(){
        return ResponseEntity.ok(statisticsService.getTotalStatisticsRaddoppio());
    }



}
