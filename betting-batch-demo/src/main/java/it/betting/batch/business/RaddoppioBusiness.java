package it.betting.batch.business;

import it.betting.batch.constant.BettingBatchCostants;
import it.betting.batch.entity.Odd;
import it.betting.batch.entity.Raddoppio;
import it.betting.batch.util.BettingUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RaddoppioBusiness {


    public static Raddoppio singleMatch(List<Odd> oddsFromDB, List<Odd> oddDaCancellare) {
        oddsFromDB = oddsFromDB.stream()
                .filter(rs -> (rs.getUno() > 1.70 && rs.getUno() <= 1.85) || (rs.getDue() > 1.70 && rs.getDue() <= 1.85))
                .filter(rs -> rs.getDateMatch().after(Date.from(LocalDate.now().atTime(11, 0, 0).toInstant(ZoneOffset.UTC))))
                .filter(rs -> !rs.isStrana())
                .collect(Collectors.toList());


        if (oddsFromDB.size() == 0)
            return null;

        oddDaCancellare.add(oddsFromDB.get(0));

        return Raddoppio.builder()
                .odds(Collections.singletonList(oddsFromDB.get(0)))
                .pubblicata(BettingUtils.nowDateForRaddoppio())
                .type(BettingBatchCostants.SINGLE_MATCH)
                .build();
    }


    public static Raddoppio twoMatches(List<Odd> oddsFromDB, List<Odd> oddDaCancellare) {

        if (!oddDaCancellare.isEmpty())
            oddDaCancellare.forEach(oddsFromDB::remove);

        oddsFromDB = oddsFromDB.stream()
                .filter(rs -> (rs.getUno() > 1.30 && rs.getUno() <= 1.50) || (rs.getDue() > 1.30 && rs.getDue() <= 1.50))
                .filter(rs -> rs.getDateMatch().after(Date.from(LocalDate.now().atTime(11, 0, 0).toInstant(ZoneOffset.UTC))))
                .filter(rs -> !rs.isStrana())
                .collect(Collectors.toList());


        if (oddsFromDB.size() <= 1)
            return null;

        List<Odd> app = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            app.add(oddsFromDB.get(i));
            oddDaCancellare.add(oddsFromDB.get(i));
        }


        return Raddoppio.builder()
                .odds(app)
                .pubblicata(BettingUtils.nowDateForRaddoppio())
                .type(BettingBatchCostants.TWO_MATCHES)
                .build();

    }

    public static Raddoppio doppiaChance(List<Odd> oddsFromDB, List<Odd> oddDaCancellare) {

        if (!oddDaCancellare.isEmpty()) {
            oddDaCancellare.forEach(oddsFromDB::remove);
        }

        oddsFromDB = oddsFromDB.stream()
                .filter(rs -> (rs.getUno() > 1.85 && rs.getUno() <= 2.05) || (rs.getDue() > 1.85 && rs.getDue() <= 2.05))
                .filter(rs -> rs.getDateMatch().after(Date.from(LocalDate.now().atTime(11, 0, 0).toInstant(ZoneOffset.UTC))))
                .filter(rs -> !rs.isStrana())
                .collect(Collectors.toList());

        if (oddsFromDB.size() < 3)
            return null;


        List<Odd> app = new ArrayList<>();

        int size = 3;

        for (int i = 0; i < size; i++)
            app.add(oddsFromDB.get(i));

        return Raddoppio.builder()
                .odds(app)
                .pubblicata(BettingUtils.nowDateForRaddoppio())
                .type(BettingBatchCostants.DOPPIA_CHANCE)
                .build();
    }

}
