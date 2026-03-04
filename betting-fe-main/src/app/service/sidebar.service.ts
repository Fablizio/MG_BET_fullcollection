import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Campionato} from '../domain/campionato';
import {environment} from '../../environments/environment';
import {NgxUiLoaderService} from 'ngx-ui-loader';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {

  options = {
    headers: {
      CODE: localStorage.getItem('CODE')
    }
  };

  constructor(private httpClient: HttpClient) {
  }


  public getCampionati(): Observable<Campionato[]> {
    return this.httpClient.get<Campionato[]>(environment.BETTING_API + 'betting/site', this.options);
  }

  public getOdds(idSite: string, spinner: NgxUiLoaderService): Observable<OddDTO[]> {
    spinner.start();
    let url = environment.BETTING_API + 'betting/listaOdds/{idSite}';
    url = url.replace('{idSite}', idSite);

    return this.httpClient
      .get<OddDTO[]>(url, this.options);
  }

  public getTodayMatch(): Observable<OddDTO[]> {
    return this.httpClient.get<OddDTO[]>(environment.BETTING_API + 'betting/today-match', this.options);
  }

  public getSmellBet(): Observable<OddDTO[]> {
    return this.httpClient.get<OddDTO[]>(environment.BETTING_API + 'betting/today-smellBet', this.options);
  }

  searchBy(percentuale: any, tipo: any): Observable<OddDTO[]> {
    let param = '?percentuale='+percentuale+'&type='+tipo
    return this.httpClient.get<OddDTO[]>(environment.BETTING_API + 'betting/filter'+param, this.options);
  }
}
