import {NgModule} from '@angular/core';
import {HeaderComponent} from './header/header.component';
import {IonicModule} from '@ionic/angular';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SocialsComponent} from './socials/socials.component';
import {EventoComponent} from './evento/evento.component';
import {CardEventiComponent} from './card-eventi/card-eventi.component';
import {MenuNazioniComponent} from './menu-nazioni/menu-nazioni.component';
import {CardOddsComponent} from './card-odds/card-odds.component';
import {CardOddsSkeletonComponent} from './card-odds-skeleton/card-odds-skeleton.component';
import {SideMenuComponent} from './side-menu/side-menu.component';
import {SpinnerComponent} from './spinner/spinner.component';

@NgModule({
    imports: [
        IonicModule,
        CommonModule,
        FormsModule
    ],
    declarations: [HeaderComponent, SideMenuComponent, SocialsComponent, EventoComponent, CardEventiComponent, CardOddsComponent, CardOddsSkeletonComponent, MenuNazioniComponent, SpinnerComponent],
    exports: [HeaderComponent, SideMenuComponent, SocialsComponent, EventoComponent, CardEventiComponent, CardOddsComponent, CardOddsSkeletonComponent, MenuNazioniComponent, SpinnerComponent]
})
export class ComponentsModule {
}
