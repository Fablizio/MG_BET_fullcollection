import { Component, OnInit } from '@angular/core';
import { BettingService } from '../betting.service';
import { Odds } from '../models/betting';

interface Campionato {
    name: string;
    odds: Odds[];
}

function sortCampionatiByName(a: Campionato, b: Campionato): number {
    return a.name.localeCompare(b.name, 'it', { sensitivity: 'base' });
}

@Component({
    selector: 'app-tabTodayMatchPage',
    templateUrl: 'tabTodayMatch.page.html',
    styleUrls: ['tabTodayMatch.page.scss'],
    providers: [BettingService]
})
export class TabTodayMatchPage implements OnInit {
    loaded = false;

    // Tutte le odds piatte, così l’ordinamento è unico e coerente
    private allOdds: Odds[] = [];

    // Dati già raggruppati per campionato, usati nel template
    campionati: Campionato[] = [];

    // Modalità
    loadSmellBet = false;

    // Stato ordinamento (legato alla UI dei filtri)
    sortBy: 'time' | 'confidence' = 'time';
    sortDirection: 'asc' | 'desc' = 'asc';

    constructor(private bettingService: BettingService) {}

    ngOnInit(): void {
        this.fetchTodayMatch();
    }

    // ==========================
    //   FETCH DATI
    // ==========================

    public fetchSmellBet(): void {
        this.loaded = false;
        this.loadSmellBet = true;

        // UX: per SmellBet ha più senso partire da confidence discendente
        this.sortBy = 'confidence';
        this.sortDirection = 'desc';

        this.bettingService.getSmellBet().subscribe(
            (res: Odds[]) => {
                this.allOdds = res || [];
                this.buildCampionatiFromOdds();
                this.loaded = true;
            },
            (err) => {
                console.error(err);
                this.loaded = true;
                // TODO: messaggio errore
            }
        );
    }

    public fetchTodayMatch(): void {
        this.loaded = false;
        this.loadSmellBet = false;

        // UX: per “Partite di oggi” ha senso default per orario crescente
        this.sortBy = 'time';
        this.sortDirection = 'asc';

        this.bettingService.todayMatch().subscribe(
            (res: Odds[]) => {
                this.allOdds = res || [];
                this.buildCampionatiFromOdds();
                this.loaded = true;
            },
            (err) => {
                console.error(err);
                this.loaded = true;
                // TODO: messaggio errore
            }
        );
    }

    // ==========================
    //   UX ORDINAMENTO
    // ==========================

    public onSortChange(field: 'time' | 'confidence'): void {
        if (this.sortBy === field) {
            return;
        }

        this.sortBy = field;

        // Default intelligenti per ogni tipo di sort
        if (field === 'time') {
            this.sortDirection = 'asc';
        } else {
            this.sortDirection = 'desc';
        }

        this.buildCampionatiFromOdds();
    }

    public toggleSortDirection(): void {
        this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
        this.buildCampionatiFromOdds();
    }

    // ==========================
    //   COSTRUZIONE CAMPIONATI
    // ==========================

    /**
     * Ricostruisce i campionati a partire da allOdds,
     * applicando l'ordinamento corrente.
     */
    private buildCampionatiFromOdds(): void {
        if (!this.allOdds || this.allOdds.length === 0) {
            this.campionati = [];
            return;
        }

        // 1) ordino le odds secondo sortBy / sortDirection
        const sorted = [...this.allOdds].sort((a, b) => this.compareOdds(a, b));

        // 2) raggruppo per campionato mantenendo l'ordine interno
        const campionatiMap = new Map<string, Odds[]>();

        for (const odd of sorted) {
            const name = odd.campionato || 'Altro';
            if (!campionatiMap.has(name)) {
                campionatiMap.set(name, []);
            }
            campionatiMap.get(name)!.push(odd);
        }

        // 3) converto in array e ordino i campionati per nome
        this.campionati = Array.from(campionatiMap.entries())
            .map(([name, odds]) => ({ name, odds }))
            .sort(sortCampionatiByName);
    }

    // ==========================
    //   COMPARATORE ODDS
    // ==========================

    private compareOdds(a: Odds, b: Odds): number {
        if (this.sortBy === 'time') {
            const tA = this.getMatchTime(a);
            const tB = this.getMatchTime(b);

            if (tA === null && tB === null) return 0;
            if (tA === null) return 1;  // senza data vanno in fondo
            if (tB === null) return -1;

            return this.sortDirection === 'asc' ? tA - tB : tB - tA;
        } else {
            const cA = this.getConfidence(a);
            const cB = this.getConfidence(b);

            if (cA === null && cB === null) return 0;
            if (cA === null) return 1;
            if (cB === null) return -1;

            return this.sortDirection === 'asc' ? cA - cB : cB - cA;
        }
    }

    // ==========================
    //   HELPER: ORARIO PARTITA
    // ==========================

    /**
     * Converte `dataEvent` (formato "DD/MM/YYYY HH:mm") in timestamp.
     */
    private getMatchTime(odd: Odds): number | null {
        if (!odd.dataEvent) {
            return null;
        }

        return this.parseDataEvent(odd.dataEvent);
    }

    /**
     * Parso stringa tipo "29/11/2025 16:00" -> Date.getTime()
     */
    private parseDataEvent(dateStr: string): number | null {
        if (!dateStr) {
            return null;
        }

        const [datePart, timePart] = dateStr.split(' ');
        if (!datePart || !timePart) {
            return null;
        }

        const [dayStr, monthStr, yearStr] = datePart.split('/');
        const [hourStr, minuteStr] = timePart.split(':');

        const day = parseInt(dayStr, 10);
        const month = parseInt(monthStr, 10) - 1; // 0-based
        const year = parseInt(yearStr, 10);
        const hour = parseInt(hourStr, 10);
        const minute = parseInt(minuteStr, 10);

        if (
            isNaN(day) ||
            isNaN(month) ||
            isNaN(year) ||
            isNaN(hour) ||
            isNaN(minute)
        ) {
            return null;
        }

        const d = new Date(year, month, day, hour, minute);
        const time = d.getTime();
        return isNaN(time) ? null : time;
    }

    // ==========================
    //   HELPER: CONFIDENCE
    // ==========================

    /**
     * Usa direttamente predictionConfidence dall'oggetto Odds.
     */
    private getConfidence(odd: Odds): number | null {
        if (typeof odd.predictionConfidence === 'number') {
            return odd.predictionConfidence;
        }
        return null;
    }
}
