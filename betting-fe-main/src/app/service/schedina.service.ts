import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Schedina} from '../domain/schedina';
import {environment} from '../../environments/environment';
import {Raddoppio} from '../domain/raddoppio';

@Injectable({
  providedIn: 'root'
})
export class SchedinaService {

  constructor(private httpClient: HttpClient) {
  }


  options = {
    headers: {
      CODE: localStorage.getItem('CODE')
    }
  };

  getRandomSchedina(): Observable<Schedina[]> {
    return this.httpClient.get<Schedina[]>(environment.BETTING_API + 'betting/genera-schedina', this.options);
  }

  getSingleMatch(): Observable<Schedina[]> {
    return this.httpClient.get<Schedina[]>(environment.BETTING_API + 'betting/getSingleMatch', this.options);
  }

  getTwoMatches(): Observable<Schedina[]> {
    return this.httpClient.get<Schedina[]>(environment.BETTING_API + 'betting/getTwoMatchs', this.options);
  }

  getDoppiaChance(): Observable<Schedina[]> {
    return this.httpClient.get<Schedina[]>(environment.BETTING_API + 'betting/getDoppiaChance', this.options);
  }

  getHistoryRaddoppio(): Observable<Raddoppio[]> {
    return this.httpClient.get<Raddoppio[]>(environment.BETTING_API + 'betting/raddoppio/history', this.options);
  }
  getHistorySmellBett(date:string): Observable<OddDTO[]> {
    return this.httpClient.get<OddDTO[]>(environment.BETTING_API + 'history/smell-bet?date='+date, this.options);
  }
  getHistoryTodayMatch(date:string): Observable<OddDTO[]> {
    return this.httpClient.get<OddDTO[]>(environment.BETTING_API + 'history/today-match?date='+date, this.options);
  }

  getHistoryRaddoppioWithParams(oneMonth: boolean, threeMonths: boolean, sixMonths: boolean): Observable<Raddoppio[]> {

    let params = 'oneMonth=true';

    if (threeMonths) {
      params = 'threeMonths=true';
    }

    if (sixMonths) {
      params = 'sixMonths=true';
    }

    console.log(params)

    return this.httpClient.get<Raddoppio[]>(environment.BETTING_API + 'betting/raddoppio/params?'.concat(params), this.options);
  }
}

