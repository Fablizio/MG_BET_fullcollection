export interface Evento {
    campionato: string;
    dateMatch: string;
    prediction: string;
    presa: boolean;
    quota: number;
    team: string;
}

export interface Nazione {
    territorio: string;
    sites: Site[];
    open: boolean;
}

export interface Site {
    id: number;
    campionato: string;
}

export interface Odds {
    showNote: boolean;
    campionato: string;
    dataAggiornamento: string;
    dataEvent: string;
    due: number;
    prediction: string;
    presa: boolean;
    result: string;
    team: string;
    uno: number;
    predictionNote: string;
    x: number;
    predictionConfidence: number;
    quotaInizialeUno: number;
    quotaInizialeX: number;
    quotaInizialeDue: number;
}

export interface TotalStatistics {
    data: string;
    win: number;
    lose: number;
    avgQuote: number;
}
