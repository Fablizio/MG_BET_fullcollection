import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {Observable} from "rxjs";
import {AuthService} from "../service/auth.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {
  private code: string;
  private logged: boolean = false;

  constructor(private authService: AuthService,
              private router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    this.code = this.authService.getCode();

    return new Promise((resolve) => {
      this.authService.checkUser(this.code)
        .subscribe((rs) => {
          resolve(true);
        }, err => {
          this.router.navigateByUrl('').then();
          resolve(false);
        })
    })

  }
}
