package it.smibet.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Utility {

    public static LocalDate now() {
        return LocalDateTime.now().atZone(ZoneId.of("Europe/Rome")).toLocalDate();
    }

}
