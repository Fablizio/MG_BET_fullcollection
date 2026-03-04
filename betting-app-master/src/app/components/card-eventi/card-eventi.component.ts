import {Component, Input} from '@angular/core';

import {Platform} from '@ionic/angular';
import {SplashScreen} from '@ionic-native/splash-screen/ngx';
import {StatusBar} from '@ionic-native/status-bar/ngx';
import {Evento} from '../../models/betting';

@Component({
    selector: 'app-card-eventi',
    templateUrl: 'card-eventi.component.html',
    styleUrls: ['card-eventi.component.scss']
})
export class CardEventiComponent {
    @Input()
    title: string;
    @Input()
    eventi: Evento[];

    show = false;


    totalOdds(matches: Evento[]): number {
        return matches.reduce((acc, match) => acc * match.quota, 1);
    }

}
