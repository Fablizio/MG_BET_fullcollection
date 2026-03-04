package com.betting.util;

import com.betting.dto.OddDTO;
import com.betting.entity.Odd;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@CommonsLog
public class BettingUtils {

    public static final String CALCOLO_1X = "1";
    public static final String CALCOLO_X2 = "2";

    public static double calcolaQuotaDoppiaChange(OddDTO odd, String prediction) {
        return calcoloDoppiaChance(odd.getUno(), odd.getX(), odd.getDue(), prediction);
    }

    private static double calcoloDoppiaChance(double uno, double x, double due, String prediction) {

        if(CALCOLO_1X.equals(prediction) && uno < 1.60) return uno;
        if(CALCOLO_X2.equals(prediction) && due < 1.60) return due;

        DecimalFormat df2 = new DecimalFormat("#.##");

        double result = 0;


//        Quota squadra A o B * Quota Pareggio / (Quota squadra A o B + Quota Pareggio);

        if (CALCOLO_1X.equals(prediction)) {
            result = (uno * x) / (uno + x);
        }
        if (CALCOLO_X2.equals(prediction)) {
            result = (due * x) / (due + x);
        }


        if (result <= 1) {
            result = 1.01;
        }
        return Double.parseDouble(df2.format(result).replace(",", "."));
    }


    public static double calcolaQuotaDoppiaChange(Odd odd, String prediction) {
        return calcoloDoppiaChance(odd.getUno(), odd.getX(), odd.getDue(), prediction);
    }


    public static Date add2Hours(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 2);
        return calendar.getTime();
    }

    public String changePattern(String data) {
        Date result = convertStringToDate(data, PATTERN_US);
        return new SimpleDateFormat("dd/MM/yyyy").format(result);

    }

    public static List<Integer> randomNumberNoDuplicates(int sizeList) {
        Random rng = new Random(); // Ideally just create one instance globally
        Set<Integer> generated = new LinkedHashSet<>();
        while (generated.size() < sizeList) {
            Integer next = rng.nextInt(sizeList) - 1;
            // As we're adding to a set, this will automatically do a containment check
            generated.add(next);
        }

        return new ArrayList<>(generated);
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


        if (!dataEvent.equals("") && !dataEvent.contains("Today") && !dataEvent.contains("Tomorrow")) {
            return Date.from(LocalDate.of(LocalDate.now().getYear(),
                            Integer.parseInt(dataEvent.split(" ")[0].split("\\.")[1]),
                            Integer.parseInt(dataEvent.split(" ")[0].split("\\.")[0]))
                    .atTime(Integer.parseInt(dataEvent.split(" ")[1].split(":")[0]),
                            Integer.parseInt(dataEvent.split(" ")[1].split(":")[1]))
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


        int ora = Integer.parseInt(oraDaParsare);
        int minuti = Integer.parseInt(minutiDaParsare);

        return Date.from(localDate.atTime(ora, minuti).toInstant(ZoneOffset.UTC));
    }


    public static final String PATTERN_US = "yyyy-MM-dd";

    public static Date convertStringToDate(String data, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(data);
        } catch (ParseException e) {
            log.error("Errore durante la conversione della data Exception --> ", e);
        }

        return null;
    }

    public static String convertDateToString(LocalDate date) {
        DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


    public Date nowDate() {
        return Date.from(LocalDateTime.now().withHour(2).toInstant(ZoneOffset.UTC));
    }

    public Date nowDateWithHour00AndMinut00(String dateIsoFormat) {
        LocalDate giorno = LocalDate.parse(dateIsoFormat);
        // Inizio: giorno precedente alle 23:30
        LocalDateTime startDateTime = giorno.minusDays(1).atTime(23, 30);
        ZoneId zone = ZoneId.of("Europe/Rome");
        return Date.from(startDateTime.atZone(zone).toInstant());
    }

    public Date nowDateWithHour00AndMinut00() {
        LocalDate giorno = LocalDate.now();
        // Inizio: giorno precedente alle 23:30
        LocalDateTime startDateTime = giorno.minusDays(1).atTime(23, 30);
        ZoneId zone = ZoneId.of("Europe/Rome");
        return Date.from(startDateTime.atZone(zone).toInstant());
    }

    public static Date parseDataEvent(String s) {
        if (s == null || s.isEmpty()) return null;
        try {
            ZoneId zone = ZoneId.of("Europe/Rome");
            LocalDate today = LocalDate.now(zone);

            String cleaned = s.trim().replaceAll("\\s+", " ");

            if (cleaned.startsWith("Today")) {
                String hhmm = cleaned.substring("Today".length()).trim();
                LocalTime t = LocalTime.parse(hhmm, DateTimeFormatter.ofPattern("H:mm"));
                return Date.from(ZonedDateTime.of(today, t, zone).toInstant());
            }
            if (cleaned.startsWith("Tomorrow")) {
                String hhmm = cleaned.substring("Tomorrow".length()).trim();
                LocalTime t = LocalTime.parse(hhmm, DateTimeFormatter.ofPattern("H:mm"));
                return Date.from(ZonedDateTime.of(today.plusDays(1), t, zone).toInstant());
            }
            // "dd.MM. HH:mm"
            String[] parts = cleaned.split(" ");
            if (parts.length == 2) {
                String datePart = parts[0];
                String timePart = parts[1];
                if (datePart.endsWith(".")) {
                    int year = Year.now(zone).getValue();
                    datePart = datePart + year; // es. "26.10.2025"
                }
                LocalDate d = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("d.MM.yyyy"));
                LocalTime t = LocalTime.parse(timePart, DateTimeFormatter.ofPattern("H:mm"));
                ZonedDateTime localZdt = ZonedDateTime.of(d, t, ZoneId.of("Europe/Rome"));
                ZonedDateTime utcZdt = localZdt.withZoneSameInstant(ZoneOffset.UTC);
                return Date.from(utcZdt.toInstant());
            }
        } catch (Exception e) {
            log.warn("Impossibile parsare dataEvent: '" + s + "'", e);
        }
        return null;
    }

    public Date nowDateWithHour23AndMinut59() {
        LocalDate giorno = LocalDate.now();
        // Fine: fine del giorno corrente (23:59:59)
        LocalDateTime endDateTime = giorno.atTime(LocalTime.MAX);
        ZoneId zone = ZoneId.of("Europe/Rome");
        return Date.from(endDateTime.atZone(zone).toInstant());
    }

    public Date nowDateWithHour23AndMinut59(String dateIsoFormat) {
        LocalDate giorno = LocalDate.parse(dateIsoFormat);
        // Fine: fine del giorno corrente (23:59:59)
        LocalDateTime endDateTime = giorno.atTime(LocalTime.MAX);
        ZoneId zone = ZoneId.of("Europe/Rome");
        return Date.from(endDateTime.atZone(zone).toInstant());
    }

    public Date nowDateWithHour00AndMinut00(Date date) {
        return Date.from(convertToLocalDateTimeViaInstant(date).withHour(0).withMinute(0).toInstant(ZoneOffset.UTC));
    }

    public Date nowDateWithHour23AndMinut59(Date date) {
        return Date.from(convertToLocalDateTimeViaInstant(date).withHour(23).withMinute(59).toInstant(ZoneOffset.UTC));
    }

    private LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {

        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date dateFrom(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }


}
