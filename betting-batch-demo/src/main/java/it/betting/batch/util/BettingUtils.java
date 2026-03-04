package it.betting.batch.util;

import it.betting.batch.constant.BettingBatchCostants;
import it.betting.batch.entity.Odd;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BettingUtils {


    public static final String CALCOLO_1X = "1";
    public static final String CALCOLO_X2 = "2";


    public static List<Integer> randomNumberNoDuplicates(int sizeList) {
        Random rng = new Random(); // Ideally just create one instance globally
        Set<Integer> generated = new LinkedHashSet<>();
        while (generated.size() < sizeList) {
            Integer next = rng.nextInt(sizeList) - 1;
            // As we're adding to a set, this will automatically do a containment check
            generated.add(next);
        }

        return generated.stream().collect(Collectors.toList());
    }


    public static boolean presa(String prediction, String finalResult, String type) {

        if(finalResult == null) return  false;

        String[] result = finalResult.split(":");


        if (prediction != null && result.length > 1) {


            int uno = Integer.parseInt(result[0]);
            int due = Integer.parseInt(result[1]);


            if(BettingBatchCostants.DOPPIA_CHANCE.equals(type)){
               if(uno == due)
                   return true;


                if (uno > due && prediction.equals("1X"))
                    return true;

                if (due > uno && prediction.equals("X2"))
                    return true;
            }

            if (uno > due && prediction.equals("1"))
                return true;
            if (due > uno && prediction.equals("2"))
                return true;
        }

        return false;

    }

    public static boolean presa(String prediction, String finalResult) {

        String[] resultSplittato = finalResult.split(":");

        if (prediction != null && resultSplittato.length > 1) {

            int uno = Integer.parseInt(resultSplittato[0]);
            int due = Integer.parseInt(resultSplittato[1]);

            if (uno >= due && prediction.equals("1 oppure 1X"))
                return true;

            if (due >= uno && prediction.equals("2 oppure X2"))
                return true;

            if (due != 0 && uno != 0 && prediction.equals("2 oppure goal"))
                return true;
        }


        return false;
    }

    public Date getDate(String dataEvent) {

        if (dataEvent.contains("Today")) {
            String oraEMinuti = dataEvent.split("Today")[1].trim();
            return getDateParse(oraEMinuti, false);
        }
        if (dataEvent.contains("Tomorrow")) {
            String oraEMinuti = dataEvent.split("Tomorrow")[1].trim();
            return getDateParse(oraEMinuti, true);
        }


        if (dataEvent != null && !dataEvent.equals("") && !dataEvent.contains("Today") && !dataEvent.contains("Tomorrow")) {
            return Date.from(LocalDate.of(LocalDate.now().getYear(),
                    Integer.parseInt(dataEvent.split(" ")[0].split("\\.")[1]),
                    Integer.parseInt(dataEvent.split(" ")[0].split("\\.")[0]))
                    .atTime(Integer.valueOf(dataEvent.split(" ")[1].split(":")[0]),
                            Integer.valueOf(dataEvent.split(" ")[1].split(":")[1]))
                    .toInstant(ZoneOffset.UTC));

        }

        return null;
    }

    private Date getDateParse(String oraEMinuti, boolean tomorrow) {
        String oraDaParsare = oraEMinuti.split(":")[0].trim();
        String minutiDaParsare = oraEMinuti.split(":")[1].trim();

        LocalDate localDate = LocalDate.now();

        if (tomorrow)
            localDate = localDate.plusDays(1);


        int ora = Integer.valueOf(oraDaParsare);
        int minuti = Integer.valueOf(minutiDaParsare);

        return Date.from(localDate.atTime(ora, minuti).toInstant(ZoneOffset.UTC));
    }


    public static String convertDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public Date nowDate() {
        return Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    public static Date nowDateForRaddoppio() {
        return Date.from(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC));
    }

    public Date nowDateWithHour00AndMinut00() {
        return Date.from(LocalDateTime.now().withHour(0).withMinute(0).toInstant(ZoneOffset.UTC));
    }

    public Date nowDateWithHour23AndMinut59() {
        return Date.from(LocalDateTime.now().withHour(23).withMinute(59).toInstant(ZoneOffset.UTC));
    }


    public static double calcolaQuotaDoppiaChange(Odd odd, String prediction) {

        DecimalFormat df2 = new DecimalFormat("#.##");

        double uno = odd.getUno();
        double x = odd.getX();
        double due = odd.getDue();

        double result = 0;


//        Quota squadra A o B * Quota Pareggio / (Quota squadra A o B + Quota Pareggio);

        if (CALCOLO_1X.equals(prediction)) {
            result = (uno * x) / (uno + x);
        }
        if (CALCOLO_X2.equals(prediction)) {
            result = (due * x) / (due + x);
        }

        return Double.valueOf(df2.format(result).replace(",", "."));
    }

}
