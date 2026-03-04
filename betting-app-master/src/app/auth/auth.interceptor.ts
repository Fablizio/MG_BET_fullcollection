import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError} from 'rxjs/operators';
import {AuthService} from './auth.service';
import {Observable} from 'rxjs';
import {NavController} from '@ionic/angular';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(
        private authService: AuthService,
        private navController: NavController) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (req.url.includes('auth/signin')) {
            return next.handle(req);
        }
        // Clone the request to add the new header.
        const authReq = req.clone({headers: req.headers.set('CODE', `${this.authService.code}`)});
        // send the newly created request
        return next.handle(authReq).pipe(
            catchError(err => {
                if (err.status === 401) {
                    this.navController.navigateRoot('/login');
                }
                throw err;
            })
        );
    }
}
