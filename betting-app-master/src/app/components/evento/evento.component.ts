import {Component, Input} from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import {Evento} from '../../models/betting';

@Component({
  selector: 'app-evento',
  templateUrl: 'evento.component.html',
  styleUrls: ['evento.component.scss']
})
export class EventoComponent {
    @Input()
    evento: Evento;
}
