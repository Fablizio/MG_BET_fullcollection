import {Component} from '@angular/core';
import {AuthService} from '../auth/auth.service';
import {AlertController, ModalController, NavController} from '@ionic/angular';
import {InfoPage} from './info/info.page';
import {PaymentPage} from '../payment/payment.page';

@Component({
    selector: 'app-login',
    templateUrl: './login.page.html',
    styleUrls: ['./login.page.scss'],
})
export class LoginPage {
    code: string;
    rememberCode = false;
    loading = false;

    constructor(
        private authService: AuthService,
        private navController: NavController,
        public alertController: AlertController,
        public modalController: ModalController
    ) {
    }

    async presentModalInfo() {
        const modal = await this.modalController.create({
            component: InfoPage
        });
        return await modal.present();
    }

    async presentModalPayment(renew: boolean) {
        const modal = await this.modalController.create({
            component: PaymentPage,
            componentProps: {
                renew
            }
        });
        return await modal.present();
    }

    login() {
        this.loading = true;
        this.authService.login(this.code, true).subscribe(
            () => {
                this.loading = false;
                this.navController.navigateRoot('/');
            },
            async (err) => {
                console.error(err);
                this.loading = false;
                const alert = await this.alertController.create({
                    header: 'Autenticazione non riuscita',
                    message: 'Il codice fornito non è valido o scaduto.',
                    buttons: ['OK']
                });
                await alert.present();
            }
        );
    }
}
