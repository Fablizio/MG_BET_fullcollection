import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Campionato} from '../domain/campionato';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SiteService {
  options = {
    headers: {
      CODE: localStorage.getItem('CODE')
    }
  };

  constructor(private httpClient: HttpClient) {
  }


  getAll(): Observable<any[]> {
    return this.httpClient.get<any[]>(environment.BETTING_API + 'betting/site', this.options);
  }

  enabled(b: boolean, id: string): Observable<any[]> {
    return this.httpClient.put<any[]>(environment.BETTING_API + 'site/enabled/' + id, {
      active: b
    }, this.options);
  }

  save(territorio, campionato, link, active) {
    return this.httpClient.post<any[]>(environment.BETTING_API + 'site/insert', {
      territorio, campionato, link, active
    }, this.options);
  }

}
