import {Component, Input, OnChanges} from '@angular/core';

import {Platform} from '@ionic/angular';
import {SplashScreen} from '@ionic-native/splash-screen/ngx';
import {StatusBar} from '@ionic-native/status-bar/ngx';
import {Evento, Odds} from '../../models/betting';

@Component({
    selector: 'app-card-odds',
    templateUrl: 'card-odds.component.html',
    styleUrls: ['card-odds.component.scss']
})
export class CardOddsComponent implements OnChanges {
    @Input()
    campionato: string;
    @Input()
    odds: Odds[];
    @Input()
    history = false;
    @Input()
    showPercentage = false;
    @Input()
    showPredictionConfidence = false;

    @Input()
    showDetail = true;

    ngOnChanges() {
        if (this.odds && this.odds.length > 0) {
            this.odds = this.odds.map(o => ({
                ...o,
                showNote: false   // evita di sovrascriverlo se già esiste
            }));
        }
    }

}
