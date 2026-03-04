package com.betting.service.impl;

import com.betting.converter.Converter;
import com.betting.dto.RaddoppioDTO;
import com.betting.dto.SchedinaDTO;
import com.betting.entity.Raddoppio;
import com.betting.repository.RaddoppioRepository;
import com.betting.service.IRaddoppioService;
import com.betting.util.BettingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RaddoppioServiceImpl implements IRaddoppioService {

    @Autowired
    RaddoppioRepository raddoppioRepository;


    @Autowired
    BettingUtils bettingUtils;

    @Override
    public List<SchedinaDTO> getSingleMatch() {
        Optional<Raddoppio> result = raddoppioRepository.findByPubblicataGreaterThanAndPubblicataLessThanAndTypeEquals(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59(), "SINGLE_MATCH");

        return getSchedinaFromRaddoppio(result);
    }

    private List<SchedinaDTO> getSchedinaFromRaddoppio(Optional<Raddoppio> result) {
        return result.map(raddoppio -> raddoppio
                .getOdds()
                .stream()
                .map(Converter::convertFromEntity)
                .collect(Collectors.toList())
                .stream()
                .map(Converter::convertFromDTO)
                .collect(Collectors.toList())).orElseGet(ArrayList::new);

    }

    @Override
    public List<RaddoppioDTO> getHistory() {

        return raddoppioRepository.findAllByOrderByPubblicataDesc()
                .stream()
                .map(Converter::convertFromEntity)
                .filter(rs -> rs.getDataRaddoppio() != null)
                .collect(Collectors.toList());

    }

    @Override
    public List<RaddoppioDTO> getHistory(boolean oneMonth, boolean threeMonths, boolean sixMonths) {

        Date date = BettingUtils.dateFrom(LocalDateTime.now().minusMonths(1).withHour(0).withMinute(0));

        if (threeMonths) {
            date = BettingUtils.dateFrom(LocalDateTime.now().minusMonths(3));
        } else if (sixMonths) {
            date = BettingUtils.dateFrom(LocalDateTime.now().minusMonths(6));
        }


        return raddoppioRepository.findByPubblicataGreaterThanEqualOrderByPubblicataDesc(date)
                .stream()
                .map(Converter::convertFromEntity)
                .filter(rs -> rs.getDataRaddoppio() != null)
                .collect(Collectors.toList());

    }

    @Override
    public List<SchedinaDTO> getTwoMatchs() {
        Optional<Raddoppio> result = raddoppioRepository.findByPubblicataGreaterThanAndPubblicataLessThanAndTypeEquals(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59(), "TWO_MATCHES");
        return getSchedinaFromRaddoppio(result);
    }

    @Override
    public List<SchedinaDTO> getDoppiaChance() {
        Optional<Raddoppio> result = raddoppioRepository.findByPubblicataGreaterThanAndPubblicataLessThanAndTypeEquals(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59(), "DOPPIA_CHANCE");

        List<SchedinaDTO> schedina = getSchedinaFromRaddoppio(result);


        schedina.forEach(schedinaDTO -> result.get().getOdds().forEach(odd -> {
            if (schedinaDTO.getTeam().equals(odd.getTeam())) {
                schedinaDTO.setQuota(BettingUtils.calcolaQuotaDoppiaChange(odd, schedinaDTO.getPrediction()));
                schedinaDTO.setPrediction(schedinaDTO.getPrediction().equals("1") ? "1X" : "X2");
            }
        }));


        return schedina;
    }
}
