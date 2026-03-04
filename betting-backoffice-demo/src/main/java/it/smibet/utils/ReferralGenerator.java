package it.smibet.utils;

import java.security.SecureRandom;

public class ReferralGenerator {
    private static final String PREFIX = "MGBET";
    // caratteri consentiti nel suffisso (maiuscole e cifre)
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // evita I, O, 0, 1 per chiarezza
    private static final SecureRandom random = new SecureRandom();

    /**
     * Genera un codice amico che inizia con "MGBET" seguito da un suffisso casuale.
     * @param suffixLength lunghezza del suffisso (es. 6)
     * @return codice completo
     */
    public static String generateCode(int suffixLength) {
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < suffixLength; i++) {
            int idx = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(idx));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Esempi: genera 5 codici con suffisso di 6 caratteri
        int suffixLength = 6;
        for (int i = 0; i < 5; i++) {
            System.out.println(generateCode(suffixLength));
        }
    }
}