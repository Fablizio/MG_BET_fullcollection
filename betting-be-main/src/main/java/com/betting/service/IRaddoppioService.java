package com.betting.service;

import com.betting.dto.RaddoppioDTO;
import com.betting.dto.SchedinaDTO;

import java.util.List;

public interface IRaddoppioService {


    List<SchedinaDTO> getSingleMatch();

    List<SchedinaDTO> getTwoMatchs();

    List<SchedinaDTO> getDoppiaChance();



    List<RaddoppioDTO> getHistory();

    List<RaddoppioDTO> getHistory(boolean oneMonth, boolean threeMonths, boolean sixMonths);
}
