package it.betting.batch.converter;

import it.betting.batch.dto.OddDTO;
import it.betting.batch.dto.SchedinaDTO;
import it.betting.batch.entity.Odd;

import java.text.SimpleDateFormat;

public class Converter {

    public static SchedinaDTO convertFromDTO(OddDTO odd) {
        String prediction = odd.getPrediction().split("oppure")[0].trim();
        return SchedinaDTO.builder()
                .campionato(odd.getCampionato())
                .team(odd.getTeam())
                .prediction(prediction)
                .quota(prediction.equals("1") ? odd.getUno() : odd.getDue())
                .dateMatch(odd.getDataEvent())
                .presa(odd.isPresa())
                .build();

    }

    public static OddDTO convertFromEntity(Odd odd) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return OddDTO.builder()
                .team(odd.getTeam())
                .uno(odd.getUno())
                .due(odd.getDue())
                .x(odd.getX())
                .dataEvent(sdf.format(odd.getDateMatch()))
                .result(odd.getFinalResult())
                .presa(odd.isPresa())
                .prediction(odd.getPrediction())
                .campionato(odd.getSite().getTerritorio() + " - " + odd.getSite().getCampionato())
                .build();
    }
}
