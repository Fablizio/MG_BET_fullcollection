import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {Account} from '../models/auth';
import {map, switchMap} from 'rxjs/operators';
import {AlertController} from '@ionic/angular';
import * as moment from 'moment';
import {Router} from "@angular/router";

const DAY_BEFORE_EXPIRE = 5;

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    account: Account;

    constructor(private httpClient: HttpClient,
                private router: Router,
                private alertController: AlertController) {
    }

    get code(): string {
        return this.account ? this.account.code : '';
    }

    init() {
        const code = this.getCode();
        if (code) {
            this.account = {code};
            this.fetchAccount().subscribe(
                (res) => console.log('account restored'),
                (err) => console.error(err)
            );
        }

    }

    login(code: string, rememberCode: boolean): Observable<Account> {
        return this.httpClient.post<any>(`${environment.endpoint}/auth/signin`, {}, {
            headers: {
                CODE: code
            }
        }).pipe(
            switchMap((res) => {
                if (rememberCode) {
                    this.saveCode(code);
                }
                this.account = {
                    code,
                };
                return this.fetchAccount();
            })
        );
    }

    logout() {
        this.account = undefined;
        this.removeCode();
    }

    public fetchAccount(): Observable<Account> {
        return this.httpClient.get<any>(`${environment.endpoint}/user/details`).pipe(
            map(res => {
                this.account = {
                    ...this.account,
                    expireDate: res.expireDate,
                    username: res.username,
                    nickName: res.nickName,
                    friendCode: res.friendCode,
                    friendCodeActive: res.friendCodeActive,
                    subscription: res.subscription


                };
                this.checkExpireDate();
                return this.account;
            })
        );
    }

    private async checkExpireDate() {
        const dayUntilExpire = moment(this.account.expireDate, 'DD/MM/YYYY')
            .diff(moment(), 'day', false);

        if (dayUntilExpire <= DAY_BEFORE_EXPIRE) {
            const alert = await this.alertController.create({
                header: 'Il tuo abbonamento è in scadenza',
                message: `
        Il tuo abbonamento sta per scadere.<br><br>
        <strong>Data di scadenza:</strong> ${this.account.expireDate}<br><br>
        Rinnova per continuare ad utilizzare tutte le funzionalità di <strong>MG BET</strong>.
      `,
                buttons: [
                    {
                        text: 'Rinnova ora',
                        handler: () => {
                            // 🔹 Vai alla tab Account
                            this.router.navigate(['/tabs', 'tabAccount']);
                        }
                    },
                    {
                        text: 'Più tardi',
                        role: 'cancel'
                    }
                ],
                cssClass: 'mg-expire-alert',
                mode: 'md'
            });

            await alert.present();
        }
    }



    private getCode(): string {
        return localStorage.getItem('code');
    }

    private saveCode(code: string) {
        localStorage.setItem('code', code);
    }

    private removeCode() {
        localStorage.removeItem('code');
    }

    public redeem(friendCodeAdded: string) {
        return this.httpClient.post<any>(`${environment.endpoint}/user/apply-friend-code`, {friendCode: friendCodeAdded});
    }

    updateDiscordUsername(discordUsername: string) {
        return this.httpClient.put<any>(`${environment.endpoint}/user/update/discord-username/${discordUsername}`, {});
    }
}
