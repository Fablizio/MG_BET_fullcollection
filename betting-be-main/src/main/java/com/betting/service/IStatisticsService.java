package com.betting.service;

import com.betting.dto.StatisticsDTO;

import java.util.List;

public interface IStatisticsService {

    StatisticsDTO getTotalStatistics();

    List<StatisticsDTO> getTotalStatisticsByCampionato(String idCampionato);

    StatisticsDTO getTotalPieChartStatisticsByCampionato(String idCampionato);

    StatisticsDTO getTotalStatisticsRaddoppio();

}
