import {Injectable} from '@angular/core';
import {NgxUiLoaderService} from "ngx-ui-loader";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class HistoryService {

  constructor(private httpClient: HttpClient,
              private spinner: NgxUiLoaderService) {
  }

  options = {
    headers: {
      CODE: localStorage.getItem('CODE')
    }
  };

  getOdds(id: string, spinner: NgxUiLoaderService): Observable<OddDTO[]> {

    spinner.start();
    let url = environment.BETTING_API + 'history/{idSite}';
    url = url.replace('{idSite}', id);

    return this.httpClient.get<OddDTO[]>(url, this.options);

  }
}
