import {Component} from '@angular/core';
import {ModalController} from '@ionic/angular';

@Component({
    selector: 'app-info',
    templateUrl: './info.page.html',
    styleUrls: ['./info.page.scss'],
})
export class InfoPage {
    constructor(private modalController: ModalController) {
    }

    dismiss() {
        this.modalController.dismiss();
    }
}
