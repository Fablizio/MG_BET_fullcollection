import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {TotalStatistics} from "../domain/total-statistics";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private httpClient:HttpClient) { }

  options = {
    headers: {
      CODE: localStorage.getItem('CODE')
    }
  };

  public getChangeOfOdds(idOdd:string):Observable<ChangeOfOdds[]>{
    return this.httpClient.get<ChangeOfOdds[]>(environment.BETTING_API+'betting/changeOfOdds/'+idOdd,this.options);
  }
  public getTotalStatisticsRaddoppi():Observable<TotalStatistics>{
    return this.httpClient.get<TotalStatistics>(environment.BETTING_API+'betting/statistics/raddoppio',this.options);
  }

  public getTotalStatistics():Observable<TotalStatistics>{
    return this.httpClient.get<TotalStatistics>(environment.BETTING_API+'betting/statistics/total',this.options);
  }

  public getTotalStatisticsByCampionato(id:string):Observable<TotalStatistics[]> {
    return this.httpClient.get<TotalStatistics[]>(environment.BETTING_API+'betting/statistics/campionato/'+id,this.options);
  }

  public getPieChartDataByCampionation(id:string):Observable<TotalStatistics>{
    return this.httpClient.get<TotalStatistics>(environment.BETTING_API+'betting/statistics/total/campionato/'+id,this.options);
  }
  public findAll():Observable<any[]>{
    return this.httpClient.get<any[]>(environment.BETTING_API+'history/all-matches',this.options);
  }

}
