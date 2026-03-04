import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthComponent} from './auth/auth.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {AuthGuardService} from './guards/auth-guard.service';
import {TablePredictionComponent} from './table-prediction/table-prediction.component';
import {HomePageComponent} from './home-page/home-page.component';
import {FeedRssComponent} from "./feed-rss/feed-rss.component";
import {HistoryComponent} from "./history/history.component";
import {GeneraSchedinaComponent} from "./genera-schedina/genera-schedina.component";
import {ChiSiamoComponent} from "./chi-siamo/chi-siamo.component";
import {HistoryRaddoppiComponent} from "./history-raddoppi/history-raddoppi.component";
import {TodayMatchComponent} from "./today-match/today-match.component";
import {PagamentoComponent} from './pagamento/pagamento.component';
import {StoricoRaddoppioComponent} from './storico-raddoppio/storico-raddoppio.component';
import {ChangeOfOddsComponent} from './change-of-odds/change-of-odds.component';
import {HistorySmellBetComponent} from './history-smell-bet/history-smell-bet.component';
import {HistoryTodayMatchComponent} from './history-today-match/history-today-match.component';
import {SiteComponent} from './site/site.component';
import {StatiumLandingComponent} from './statium-landing/statium-landing.component';

const routes: Routes = [
  {path: '', component: StatiumLandingComponent},
  {path: 'auth', component: AuthComponent},
  {path: 'statium', component: StatiumLandingComponent},
  {path: 'pagamento', component: PagamentoComponent},
  {path: 'home', component: HomePageComponent,canActivate:[AuthGuardService]},
  {path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuardService]},
  {path: 'view/:territorio', component: TablePredictionComponent, canActivate: [AuthGuardService]},
  {path: 'notizia/:idSite', component: FeedRssComponent, canActivate: [AuthGuardService]},
  {path: 'history/:territorio', component: HistoryComponent, canActivate: [AuthGuardService]},
  {path: 'genera-schedina', component: GeneraSchedinaComponent, canActivate: [AuthGuardService]},
  {path: 'chi-siamo', component: ChiSiamoComponent, canActivate: [AuthGuardService]},
  {path: 'history-raddoppio', component: HistoryRaddoppiComponent, canActivate: [AuthGuardService]},
  {path: 'today-match/:type', component: TodayMatchComponent, canActivate: [AuthGuardService]},
  {path: 'storico-raddoppio', component: StoricoRaddoppioComponent, canActivate: [AuthGuardService]},
  {path: 'change-of-odds/:idOdd/:nomeSquadra', component: ChangeOfOddsComponent, canActivate: [AuthGuardService]},
  {path: 'history-smell-bet', component: HistorySmellBetComponent, canActivate: [AuthGuardService]},
  {path: 'history-today-match', component: HistoryTodayMatchComponent, canActivate: [AuthGuardService]},
  {path: 'site', component: SiteComponent, canActivate: [AuthGuardService]},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
