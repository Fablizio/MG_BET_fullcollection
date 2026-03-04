import {Component} from '@angular/core';
import {ModalController, NavParams} from '@ionic/angular';
import {GoogleAnalyticsService} from '../google.analytics.service';

@Component({
    selector: 'app-payment',
    templateUrl: './payment.page.html',
    styleUrls: ['./payment.page.scss'],
})
export class PaymentPage {
    renew: boolean;

    eventCategory = 'paymentPage';
    eventAction: string;

    constructor(
        private modalController: ModalController,
        private navParams: NavParams,
        private googleAnalyticsService: GoogleAnalyticsService
        ) {
    }

    ionViewDidEnter() {
        this.renew = this.navParams.get('renew');
        this.eventAction = this.renew ? 'Renew' : 'New';
        this.googleAnalyticsService.eventEmitter(this.eventCategory, `${this.eventAction} open`);
    }

    dismiss() {
        this.googleAnalyticsService.eventEmitter(this.eventCategory, `${this.eventAction} close`);
        this.modalController.dismiss();
    }

    trackClick(amount: number) {
        this.googleAnalyticsService.eventEmitter(this.eventCategory, `${this.eventAction} click ${amount}`);
    }
}
