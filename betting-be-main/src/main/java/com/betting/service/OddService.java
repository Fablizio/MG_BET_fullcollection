package com.betting.service;

import com.betting.dto.FilterRequestDTO;
import com.betting.dto.LinkDTO;
import com.betting.dto.OddDTO;
import com.betting.dto.SchedinaDTO;

import java.util.List;

public interface OddService {

    List<OddDTO> getOddsByTeam(String idTeam);

    List<OddDTO> getOddsBySite(String idSite);

    List<OddDTO> getHistoryByIdTeam(String idTeam);

    List<SchedinaDTO> generaSchedina();

    List<OddDTO> getTodayMatch();

    LinkDTO getLink();

    List<OddDTO> getTodaySmellBet();

    List<OddDTO> getHistorySmellBet(String date);

    List<OddDTO>  getHistoryTodayMatch(String date);

    List<OddDTO> getByFilter(FilterRequestDTO filterRequestDTO);

    List<OddDTO> findAll();

}
