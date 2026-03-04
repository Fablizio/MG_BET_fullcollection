package com.betting.service.impl;

import com.betting.converter.StatisticsConvert;
import com.betting.dto.OddDTO;
import com.betting.dto.RaddoppioDTO;
import com.betting.dto.SchedinaDTO;
import com.betting.dto.StatisticsDTO;
import com.betting.entity.Odd;
import com.betting.entity.Site;
import com.betting.repository.SiteRepository;
import com.betting.repository.StatisticsRepository;
import com.betting.service.IRaddoppioService;
import com.betting.service.IStatisticsService;
import com.betting.util.BettingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements IStatisticsService {


    @Autowired
    StatisticsRepository statisticsRepository;

    @Autowired
    BettingUtils bettingUtils;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    private IRaddoppioService raddoppioService;

    @Override
    public StatisticsDTO getTotalStatistics() {
        return StatisticsDTO.builder()
                .lose(statisticsRepository.getTotalLose(bettingUtils.nowDate()))
                .win(statisticsRepository.getTotalWin(bettingUtils.nowDate()))
                .build();
    }

    @Override
    public List<StatisticsDTO> getTotalStatisticsByCampionato(String idCampionato) {

        Site site = siteRepository.findById(Long.valueOf(idCampionato)).get();
        List<StatisticsDTO> result = new ArrayList<>();


        statisticsRepository.findByAllDateDistinct(bettingUtils.nowDate(), site.getId())
                .forEach(rs -> {
                    List<Odd> odds = statisticsRepository.findByDateMatchGreaterThanEqualAndDateMatchLessThanEqualAndSiteAndPredictionIsNotNull(
                            bettingUtils.nowDateWithHour00AndMinut00(BettingUtils.convertStringToDate(rs, BettingUtils.PATTERN_US)),
                            bettingUtils.nowDateWithHour23AndMinut59(BettingUtils.convertStringToDate(rs, BettingUtils.PATTERN_US)),
                            site);


                    result.add(StatisticsConvert.convertForSite(odds, bettingUtils.changePattern(rs)));
                });


        return result;
    }

    @Override
    public StatisticsDTO getTotalPieChartStatisticsByCampionato(String idCampionato) {
        return StatisticsDTO.builder()
                .win(statisticsRepository.getTotalWinPieChartStatisticsByCampionato(bettingUtils.nowDate(), Long.valueOf(idCampionato)))
                .lose(statisticsRepository.getTotalLosePieChartStatisticsByCampionato(bettingUtils.nowDate(), Long.valueOf(idCampionato)))
                .build();
    }

    // ... existing code ...
    @Override
    public StatisticsDTO getTotalStatisticsRaddoppio() {

        List<RaddoppioDTO> history = raddoppioService.getHistory();
        List<RaddoppioDTO> finalRaddoppio = new ArrayList<>();

        // 1. Filtriamo la lista evitando duplicati
        for (RaddoppioDTO rs : history) {
            boolean hasFinalResult = false;
            // Controlliamo se almeno una quota ha un risultato
            for (SchedinaDTO oddDTO : rs.getOdds()) {
                if (oddDTO.getFinalResult() != null) {
                    hasFinalResult = true;
                    break; // Importante: appena ne troviamo una, usciamo dal ciclo interno
                }
            }
            // Aggiungiamo alla lista filtrata solo una volta
            if (hasFinalResult) {
                finalRaddoppio.add(rs);
            }

        }

        List<Double> quote = new ArrayList<>();
        for (RaddoppioDTO rs : finalRaddoppio) {
            rs.getOdds().forEach(schedinaDTO -> {
                // Nota: qui ho mantenuto la tua logica per le quote
                if (schedinaDTO.getFinalResult() != null && schedinaDTO.getPrediction().startsWith("1") && schedinaDTO.isPresa()) {
                    quote.add(schedinaDTO.getQuota());
                }
            });
        }

        // 2. Usiamo 'finalRaddoppio' (la lista filtrata) anche per win/lose
        return StatisticsDTO.builder()
                .win((int) finalRaddoppio.stream().filter(RaddoppioDTO::isPreso).count())
                .lose((int) finalRaddoppio.stream().filter(rs -> !rs.isPreso()).count())
                .avgQuote(quote.stream().mapToDouble(Double::doubleValue).average().orElse(0.0))
                .build();
    }
}
