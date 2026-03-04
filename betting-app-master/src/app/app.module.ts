import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouteReuseStrategy} from '@angular/router';

import {IonicModule, IonicRouteStrategy} from '@ionic/angular';
import {SplashScreen} from '@ionic-native/splash-screen/ngx';
import {StatusBar} from '@ionic-native/status-bar/ngx';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthService} from './auth/auth.service';
import {AuthInterceptor} from './auth/auth.interceptor';
import {ServiceWorkerModule} from '@angular/service-worker';
import {environment} from '../environments/environment';
import {BettingService} from './betting.service';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {PaymentPage} from './payment/payment.page';
import {ComponentsModule} from './components/components.module';
import {GoogleAnalyticsService} from './google.analytics.service';
import {FriendCodeModalComponent} from "./friend-code-modal/friend-code-modal.component";

@NgModule({
    declarations: [AppComponent, PaymentPage, FriendCodeModalComponent],
    entryComponents: [PaymentPage, FriendCodeModalComponent],
    imports: [
        BrowserModule,
        HttpClientModule,
        BrowserAnimationsModule,
        ComponentsModule,
        IonicModule.forRoot(),
        AppRoutingModule,
        ServiceWorkerModule.register('ngsw-worker.js', {enabled: environment.production})
    ],
    providers: [
        StatusBar,
        SplashScreen,
        {provide: RouteReuseStrategy, useClass: IonicRouteStrategy},
        AuthService,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AuthInterceptor,
            multi: true
        },
        GoogleAnalyticsService,
        BettingService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
