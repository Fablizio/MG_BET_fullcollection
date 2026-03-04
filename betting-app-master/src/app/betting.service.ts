import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../environments/environment';
import {Evento, Nazione, Odds, TotalStatistics} from './models/betting';
import {Observable, of} from 'rxjs';
import {tap} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class BettingService {
    private sitesStore: Nazione[];

    constructor(private httpClient: HttpClient) {
    }

    get sites() {
        if (this.sitesStore) {
            return of(this.sitesStore);
        } else {
            return this.fetchSites();
        }
    }

    getHistorySmellBett(date:string): Observable<Odds[]> {
        return this.httpClient.get<Odds[]>(environment.endpoint + '/history/smell-bet?date='+date);
    }

    public getSmellBet(): Observable<Odds[]> {
        return this.httpClient.get<Odds[]>(environment.endpoint + '/betting/today-smellBet');
    }

    public getTotalStatistics(): Observable<TotalStatistics> {
        return this.httpClient.get<TotalStatistics>(environment.endpoint + '/betting/statistics/total');
    }

    public getTotalStatisticsRaddoppi(): Observable<TotalStatistics> {
        return this.httpClient.get<TotalStatistics>(environment.endpoint + '/betting/statistics/raddoppio');
    }

    listaOdds(idSite): Observable<Odds[]> {
        return this.httpClient.get<any[]>(`${environment.endpoint}/betting/listaOdds/${idSite}`);
    }

    history(idSite): Observable<Odds[]> {
        return this.httpClient.get<any[]>(`${environment.endpoint}/history/${idSite}`);
    }

    getHistoryTodayMatch(date: string): Observable<Odds[]> {
        return this.httpClient.get<Odds[]>(environment.endpoint + '/history/today-match?date=' + date);
    }

    todayMatch(): Observable<Odds[]> {
        return this.httpClient.get<any[]>(`${environment.endpoint}/betting/today-match`);
    }

    singleMatch(): Observable<Evento[]> {
        return this.httpClient.get<Evento[]>(`${environment.endpoint}/betting/getSingleMatch`);
    }

    twoMatchs(): Observable<Evento[]> {
        return this.httpClient.get<Evento[]>(`${environment.endpoint}/betting/getTwoMatchs`);
    }

    doppiaChance(): Observable<Evento[]> {
        return this.httpClient.get<Evento[]>(`${environment.endpoint}/betting/getDoppiaChance`);
    }

    generaSchedina(): Observable<Evento[]> {
        return this.httpClient.get<Evento[]>(`${environment.endpoint}/betting/genera-schedina`);
    }

    private fetchSites(): Observable<Nazione[]> {
        return this.httpClient.get<any[]>(`${environment.endpoint}/betting/site`).pipe(
            tap((sites) => this.sitesStore = sites)
        );
    }
}
