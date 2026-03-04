package it.betting.batch.service;

import it.betting.batch.business.RaddoppioBusiness;
import it.betting.batch.constant.BettingBatchCostants;
import it.betting.batch.converter.Converter;
import it.betting.batch.dto.SchedinaDTO;
import it.betting.batch.entity.HandlerDoubling;
import it.betting.batch.entity.Odd;
import it.betting.batch.entity.Raddoppio;
import it.betting.batch.http.HttpClient;
import it.betting.batch.repository.HandlerDoublingRepository;
import it.betting.batch.repository.OddRepository;
import it.betting.batch.repository.RaddoppioRepository;
import it.betting.batch.repository.SiteRepository;
import it.betting.batch.util.BettingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OddServiceImpl implements OddService {

    @Autowired
    OddRepository oddRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    BettingUtils bettingUtils;

    @Autowired
    RaddoppioRepository raddoppioRepository;

    @Autowired
    HttpClient httpClient;

    @Autowired
    HandlerDoublingRepository handlerDoublingRepository;

    @Override
    public Odd findByHomeTeamAndOutHomeTeam(String team) {
        Optional<Odd> oddOptional = oddRepository.findByTeamEquals(team);
        return oddOptional.isPresent() ? oddOptional.get() : null;
    }

    @Override
    public void save(Odd odd) {
        oddRepository.save(odd);
    }

    @Override
    public void update(Odd entity) {
        entity.setPresa(BettingUtils.presa(entity.getPrediction(), entity.getFinalResult()));
        oddRepository.save(entity);
    }

    @Override
    public void createRaddoppio() {


        oddRepository.findByDateMatchGreaterThanAndDateMatchLessThanAndPredictionIsNotNull(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59())
                .ifPresent(odds -> {


                    List<Odd> oddDaCancellare = new ArrayList<>();

                    List<HandlerDoubling> list = handlerDoublingRepository.findAll();


                    if (list.get(0).isActiveSingleMatch() && !raddoppioRepository.existsByPubblicataGreaterThanAndPubblicataLessThanAndType(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59(), BettingBatchCostants.SINGLE_MATCH))
                        calcolaRaddoppioEInviaAlBot(RaddoppioBusiness.singleMatch(odds, oddDaCancellare));

                    if (list.get(0).isActiveTwoMatch() &&!raddoppioRepository.existsByPubblicataGreaterThanAndPubblicataLessThanAndType(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59(), BettingBatchCostants.TWO_MATCHES))
                        calcolaRaddoppioEInviaAlBot(RaddoppioBusiness.twoMatches(odds, oddDaCancellare));

                    if (list.get(0).isActiveDoublingChanceMatch() &&!raddoppioRepository.existsByPubblicataGreaterThanAndPubblicataLessThanAndType(bettingUtils.nowDateWithHour00AndMinut00(), bettingUtils.nowDateWithHour23AndMinut59(), BettingBatchCostants.DOPPIA_CHANCE))
                        calcolaRaddoppioEInviaAlBot(RaddoppioBusiness.doppiaChance(odds, oddDaCancellare));
                });

    }

    private void calcolaRaddoppioEInviaAlBot(Raddoppio raddoppio) {

        if (raddoppio == null) return;

        raddoppioRepository.save(raddoppio);

        List<SchedinaDTO> radd = Optional.of(raddoppio).get()
                .getOdds()
                .stream()
                .map(Converter::convertFromEntity)
                .collect(Collectors.toList())
                .stream()
                .map(Converter::convertFromDTO)
                .collect(Collectors.toList());


        if (raddoppio.getType().equals(BettingBatchCostants.DOPPIA_CHANCE)) {

            radd.forEach(schedinaDTO -> raddoppio.getOdds().forEach(odd -> {
                if (schedinaDTO.getTeam().equals(odd.getTeam())) {
                    schedinaDTO.setQuota(BettingUtils.calcolaQuotaDoppiaChange(odd, schedinaDTO.getPrediction()));
                    schedinaDTO.setPrediction(schedinaDTO.getPrediction().equals("1") ? "1X" : "X2");
                }
            }));
        }

        httpClient.sendRaddopio(radd);
    }
}
