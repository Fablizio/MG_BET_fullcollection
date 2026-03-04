import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';

import {AppComponent} from './app.component';
import {SidebarComponent} from './sidebar/sidebar.component';

import {PERFECT_SCROLLBAR_CONFIG, PerfectScrollbarConfigInterface, PerfectScrollbarModule} from 'ngx-perfect-scrollbar';
import {TablePredictionComponent} from './table-prediction/table-prediction.component';
import {AuthComponent} from './auth/auth.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {PageTemplateComponent} from './page-template/page-template.component';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {NgxUiLoaderModule} from 'ngx-ui-loader';
import {HomePageComponent} from './home-page/home-page.component';
import {FeedRssComponent} from './feed-rss/feed-rss.component';
import {HistoryComponent} from './history/history.component';
import {ToastrModule} from 'ngx-toastr';
import {GeneraSchedinaComponent} from './genera-schedina/genera-schedina.component';
import {ChiSiamoComponent} from './chi-siamo/chi-siamo.component';
import {HistoryRaddoppiComponent} from './history-raddoppi/history-raddoppi.component';
import {SidebarNazioniComponent} from './sidebar-nazioni/sidebar-nazioni.component';
import {TodayMatchComponent} from './today-match/today-match.component';
import {PagamentoComponent} from './pagamento/pagamento.component';
import {PaypalButtonComponent} from './paypal-button/paypal-button.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {AuthModalComponent} from './auth-modal/auth-modal.component';
import { StoricoRaddoppioComponent } from './storico-raddoppio/storico-raddoppio.component';
import { ChangeOfOddsComponent } from './change-of-odds/change-of-odds.component';
import { HistorySmellBetComponent } from './history-smell-bet/history-smell-bet.component';
import { HistoryTodayMatchComponent } from './history-today-match/history-today-match.component';
import { FilterComponent } from './filter/filter.component';
import { SiteComponent } from './site/site.component';

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};

@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    TablePredictionComponent,
    AuthComponent,
    DashboardComponent,
    PageTemplateComponent,
    HomePageComponent,
    FeedRssComponent,
    HistoryComponent,
    GeneraSchedinaComponent,
    ChiSiamoComponent,
    HistoryRaddoppiComponent,
    SidebarNazioniComponent,
    TodayMatchComponent,
    PagamentoComponent,
    PaypalButtonComponent,
    AuthModalComponent,
    StoricoRaddoppioComponent,
    ChangeOfOddsComponent,
    HistorySmellBetComponent,
    HistoryTodayMatchComponent,
    FilterComponent,
    SiteComponent,
  ],
  imports: [
    FormsModule,
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    PerfectScrollbarModule,
    NgxUiLoaderModule,
    ToastrModule.forRoot({
      timeOut: 5000,
      positionClass: 'toast-bottom-right',
      preventDuplicates: true,
    }),
    NgbModule,
  ],
  providers: [{
    provide: PERFECT_SCROLLBAR_CONFIG,
    useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG
  }],
  entryComponents: [AuthModalComponent],
  bootstrap: [AppComponent]
})
export class AppModule {
}
