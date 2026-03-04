package it.smibet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class JsonOperation {

    private JsonOperation() {
        throw new IllegalStateException("Questa è una classe di utilità non va istanziata!!!");
    }

    private static ObjectMapper mapper = new ObjectMapper(); // libreria di jaxon per la conversione

    public static <T> String objectToJson(T obj) {
        String jsonString = "";

        try {
            jsonString = mapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Errore durante la conversione da oggetto {} a string json: {}",e);
        }

        return jsonString;
    }
    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        T obj = null;
        try {
            obj = mapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("Errore durante la conversione da stringa json a oggetto {}: {}");
        }

        return obj;
    }

}

