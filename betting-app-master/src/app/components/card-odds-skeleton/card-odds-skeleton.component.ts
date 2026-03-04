import {Component, Input} from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import {Evento, Odds} from '../../models/betting';

@Component({
  selector: 'app-card-odds-skeleton',
  templateUrl: 'card-odds-skeleton.component.html',
  styleUrls: ['card-odds-skeleton.component.scss']
})
export class CardOddsSkeletonComponent {
}
