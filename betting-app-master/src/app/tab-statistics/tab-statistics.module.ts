import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {TabStatisticsComponent} from './tab-statistics.component';
import {ComponentsModule} from '../components/components.module';
import {IonicModule} from '@ionic/angular';


@NgModule({
  declarations: [TabStatisticsComponent],
  imports: [
    CommonModule,
    RouterModule.forChild([{path: '', component: TabStatisticsComponent}]),
    ComponentsModule,
    IonicModule
  ],
})
export class TabStatisticsModule { }
