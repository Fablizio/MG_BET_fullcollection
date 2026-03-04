import {NgModule} from '@angular/core';
import {PreloadAllModules, RouterModule, Routes} from '@angular/router';
import {AuthGuard} from './auth/auth.guard';

const routes: Routes = [
    {
        path: '',
        loadChildren: () => import('./tabs/tabs.module').then(m => m.TabsPageModule),
        canActivate: [AuthGuard]
    },
    {path: 'login', loadChildren: './login/login.module#LoginPageModule'},
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, {preloadingStrategy: PreloadAllModules, useHash: true})
    ],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
