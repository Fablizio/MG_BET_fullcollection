package com.betting.enumeration;

public enum ElaborationStatus {

    PENDING,        // Richiesta creata ma non ancora avviata
    PROCESSING,     // Elaborazione in corso
    COMPLETED,      // Elaborazione completata con successo
    FAILED,         // Elaborazione fallita (errore AI, timeout, ecc.)
    CANCELLED       // Annullata (opzionale, se prevedi cancellazioni o refund)
}