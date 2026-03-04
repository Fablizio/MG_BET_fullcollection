import {Component} from '@angular/core';
import {BettingService} from '../betting.service';
import {Odds} from '../models/betting';
import {OnClickEvent} from '../components/menu-nazioni/menu-nazioni.component';

@Component({
    selector: 'app-tabPrediction',
    templateUrl: 'tabPrediction.page.html',
    styleUrls: ['tabPrediction.page.scss']
})
export class TabPredictionPage {
    campionato: string;
    odds: Odds[] = [];
    loaded = false;

    constructor(
        private bettingService: BettingService
    ) {
    }

    fetchPrediction(event: OnClickEvent) {
        this.loaded = false;
        this.bettingService.listaOdds(event.id).subscribe((odds) => {
            this.odds = odds;
            this.campionato = event.campionato;
            this.loaded = true;
        });
    }

}
