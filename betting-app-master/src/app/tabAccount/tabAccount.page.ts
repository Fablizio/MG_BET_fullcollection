import {Component} from '@angular/core';
import {AuthService} from '../auth/auth.service';
import {Account} from '../models/auth';
import {AlertController, ModalController, NavController} from '@ionic/angular';
import {PaymentPage} from '../payment/payment.page';
import {environment} from '../../environments/environment';
import {FriendCodeModalComponent} from "../friend-code-modal/friend-code-modal.component";

@Component({
    selector: 'app-tabAccount',
    templateUrl: 'tabAccount.page.html',
    styleUrls: ['tabAccount.page.scss']
})
export class TabAccountPage {
    account: Account;
    version = environment.version;
    friendCodeAdded = '';


    constructor(
        private authService: AuthService,
        private navController: NavController,
        public alertController: AlertController,
        private modalController: ModalController
    ) {
    }

    ionViewWillEnter() {

        this.authService.fetchAccount().subscribe(value => {
            this.account = value;
            console.log(this.account);
        });

    }

    logout() {
        this.authService.logout();
        this.navController.navigateRoot('/login');
    }

    async presentModalPayment() {
        const modal = await this.modalController.create({
            component: PaymentPage,
            componentProps: {
                renew: true
            }
        });
        return await modal.present();
    }

    async openFriendCodeModal() {
        const modal = await this.modalController.create({
            component: FriendCodeModalComponent,
            componentProps: {
                friendCode: this.account.friendCode
            }
        });
        await modal.present();
    }

    async showReportMessage() {
        const alert = await this.alertController.create({
            header: 'Supporto & community',
            message: `
      Se riscontri un bug, un problema con l&apos;account o hai un suggerimento:<br><br>
      <strong>Scrivici a</strong><br>
      <a href="mailto:info@mgbet.it">info@mgbet.it</a><br><br>
      Oppure entra nella nostra <strong>community Discord</strong> per parlare con lo staff e gli altri utenti:<br>
      <a href="https://mee6.xyz/en/i/VZK3sEHlMX" target="_blank" rel="noopener noreferrer">
        Entra nel server Discord
      </a>
    `,
            buttons: [
                {
                    text: 'Apri email',
                    handler: () => {
                        window.location.href = 'mailto:info@mgbet.it';
                    }
                },
                {
                    text: 'Apri Discord',
                    handler: () => {
                        window.open('https://mee6.xyz/en/i/VZK3sEHlMX', '_blank');
                    }
                },
                {
                    text: 'Chiudi',
                    role: 'cancel'
                }
            ],
            cssClass: 'mg-report-alert',
            mode: 'md'
        });

        await alert.present();
    }



    public redeem() {
        console.log(this.friendCodeAdded);
        this.authService.redeem(this.friendCodeAdded)
            .subscribe(value => {
                this.showAlert('Codice aggiunto con successo! Sono stati aggiunti 5 giorni in più al tuo abbonamento', 'Informazioni').then();
                this.ionViewWillEnter();
                this.friendCodeAdded = '';
            }, error => {
                this.showAlert(error.error.message, 'Errore').then();
            });
    }

    async showAlert(text: string, header: string) {
        const alert = await this.alertController.create({
            header,
            // converto eventuali \n in <br> così i messaggi multi-riga si vedono bene
            message: text ? text.replace(/\n/g, '<br>') : '',
            buttons: [
                {
                    text: 'OK',
                    role: 'cancel'
                }
            ],
            cssClass: 'mg-generic-alert',
            mode: 'md'
        });

        await alert.present();
    }


    public async copyCode() {
        await navigator.clipboard.writeText(this.account.friendCode);
        this.showAlert('Codice copiato', 'Informazioni');
    }

    public addDiscordUsername() {
        console.log(this.account.discordUsername);
        this.authService.updateDiscordUsername(this.account.discordUsername)
            .subscribe(value => {
                    this.showAlert('Username Discord aggiornato con successo!', 'Informazioni');
                    this.ionViewWillEnter();
                }, error => {
                    console.log(error);
                    console.log(error.error.message);
                    this.showAlert(error.error.message, 'Errore');
                }
            );

    }
}
