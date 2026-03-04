import { Component, Input } from '@angular/core';
import { ModalController, ToastController } from '@ionic/angular';

@Component({
    selector: 'app-friend-code-modal',
    templateUrl: './friend-code-modal.component.html',
    styleUrls: ['./friend-code-modal.component.scss'],
})
export class FriendCodeModalComponent {

    @Input() friendCode: string; // riceviamo il codice dal parent

    constructor(
        private modalCtrl: ModalController,
        private toastCtrl: ToastController
    ) { }

    dismiss() {
        this.modalCtrl.dismiss();
    }

    async copyCode() {
        await navigator.clipboard.writeText(this.friendCode);
        const toast = await this.toastCtrl.create({
            message: 'Codice copiato!',
            duration: 2000,
            color: 'success'
        });
        toast.present();
    }

}