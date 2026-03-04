import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TabsPage } from './tabs.page';

const routes: Routes = [
  {
    path: 'tabs',
    component: TabsPage,
    children: [
      {
        path: 'tabHome',
        children: [
          {
            path: '',
            loadChildren: () =>
                import('../tabHome/tabHome.module').then(m => m.TabHomePageModule)
          }
        ]
      },
      {
        path: 'tabTodayMatch',
        children: [
          {
            path: '',
            loadChildren: () =>
                import('../tabTodayMatch/tabTodayMatch.module').then(m => m.TabTodayMatchPageModule)
          }
        ]
      },
      {
        path: 'tabPrediction',
        children: [
          {
            path: '',
            loadChildren: () =>
              import('../tabPrediction/tabPrediction.module').then(m => m.TabPredictionPageModule)
          }
        ]
      },
      {
        path: 'tabHistory',
        children: [
          {
            path: '',
            loadChildren: () =>
              import('../tabHistory/tabHistory.module').then(m => m.TabHistoryPageModule)
          }
        ]
      },
      {
        path: 'tabNews',
        children: [
          {
            path: '',
            loadChildren: () =>
              import('../tabNews/tabNews.module').then(m => m.TabNewsPageModule)
          }
        ]
      },
      {
        path: 'tabAccount',
        children: [
          {
            path: '',
            loadChildren: () =>
                import('../tabAccount/tabAccount.module').then(m => m.TabAccountPageModule)
          }
        ]
      },
      {
        path: 'tabStatistics',
        children: [
          {
            path: '',
            loadChildren: () =>
                import('../tab-statistics/tab-statistics.module').then(m => m.TabStatisticsModule)
          }
        ]
      },
      {
        path: '',
        redirectTo: '/tabs/tabHome',
        pathMatch: 'full'
      }
    ]
  },
  {
    path: '',
    redirectTo: '/tabs/tabHome',
    pathMatch: 'full'
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TabsPageRoutingModule {}
