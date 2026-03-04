import { Component, OnInit } from '@angular/core';
import {SchedinaService} from '../service/schedina.service';
import {NgxUiLoaderService} from 'ngx-ui-loader';
import {NgbCalendar, NgbDatepickerConfig, NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {BehaviorSubject} from 'rxjs';
import * as moment from 'moment';

@Component({
  selector: 'app-history-today-match',
  templateUrl: './history-today-match.component.html',
  styleUrls: ['./history-today-match.component.css']
})
export class HistoryTodayMatchComponent implements OnInit {

  constructor(private schedinaService: SchedinaService,
              private spinner: NgxUiLoaderService,
              private config: NgbDatepickerConfig,
              private calendar: NgbCalendar) {
  }

  listaOdds: BehaviorSubject<OddDTO[]> = new BehaviorSubject<OddDTO[]>([]);
  model: NgbDateStruct = {year: moment().year(), month: moment().month()+1, day: moment().date() - 1};

  first: boolean;


  ngOnInit() {
    let date = moment().subtract(1, 'days').format('YYYY-MM-DD');
    this.getHistoryTodayMatch(date);
  }


  search() {

    let date = moment().set({
      'year': this.model.year,
      'month': this.model.month,
      'date': this.model.day
    }).subtract(1, 'month').format('YYYY-MM-DD');

    console.log(date);

    this.getHistoryTodayMatch(date);

  }

  getHistoryTodayMatch(date) {
    this.spinner.start();
    this.schedinaService.getHistoryTodayMatch(date)
      .subscribe(rs => {
        this.listaOdds.next(rs);
        this.spinner.stop();
      }, error => this.spinner.stop());
  }

}
