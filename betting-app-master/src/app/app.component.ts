import {Component} from '@angular/core';

import {Platform} from '@ionic/angular';
import {SplashScreen} from '@ionic-native/splash-screen/ngx';
import {StatusBar} from '@ionic-native/status-bar/ngx';
import {NavigationEnd, Router} from '@angular/router';
import {AuthService} from './auth/auth.service';

// declare ga as a function to set and sent the events
declare let ga: Function;

@Component({
    selector: 'app-root',
    templateUrl: 'app.component.html',
    styleUrls: ['app.component.scss']
})
export class AppComponent {
    constructor(
        private platform: Platform,
        private splashScreen: SplashScreen,
        private statusBar: StatusBar,
        private router: Router,
	private authService: AuthService
    ) {
        this.initializeApp();
    }

    initializeApp() {
        this.platform.ready().then(() => {
            this.statusBar.styleDefault();
            this.statusBar.backgroundColorByHexString('#343a40');
            this.splashScreen.hide();
	    this.authService.init();

            this.router.events.subscribe((event: any) => {
                if (event instanceof NavigationEnd) {
                    ga('set', 'page', event.urlAfterRedirects);
                    ga('send', 'pageview');
                }
            });
        });
    }
}
