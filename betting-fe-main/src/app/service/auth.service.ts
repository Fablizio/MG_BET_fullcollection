import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpClient: HttpClient) {
  }

  public checkUser(code: string): Observable<any>{
    return this.httpClient.post(environment.BETTING_API+'auth/signin', {}, {
      headers: {'code': code}
    });

  }

  public storeCode(code:string):void{
    localStorage.setItem('CODE',code)
  }

  public getCode(): string{
    return localStorage.getItem('CODE');
  }

  public logOut():void {
    localStorage.removeItem('CODE');
    localStorage.removeItem('expiration');
  }

  storeExpiration(date: string) {
    localStorage.setItem('expiration',date);
  }

  getExpireDate():string {
    return localStorage.getItem('expiration');
  }
}
