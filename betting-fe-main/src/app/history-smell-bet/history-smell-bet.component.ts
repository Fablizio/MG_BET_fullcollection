import {Component, OnInit} from '@angular/core';
import {SchedinaService} from '../service/schedina.service';
import * as moment from 'moment';
import {BehaviorSubject, config} from 'rxjs';
import {NgbCalendar, NgbDatepickerConfig, NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';
import {NgxUiLoaderService} from 'ngx-ui-loader';

@Component({
  selector: 'app-history-smell-bet',
  templateUrl: './history-smell-bet.component.html',
  styleUrls: ['./history-smell-bet.component.css']
})
export class HistorySmellBetComponent implements OnInit {

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
    this.getHistorySmellBet(date);
  }


  search() {

    let date = moment().set({
      'year': this.model.year,
      'month': this.model.month,
      'date': this.model.day
    }).subtract(1, 'month').format('YYYY-MM-DD');

    console.log(date);

    this.getHistorySmellBet(date);

  }

  getHistorySmellBet(date) {
    this.spinner.start();
    this.schedinaService.getHistorySmellBett(date)
      .subscribe(rs => {
        this.listaOdds.next(rs);
        this.spinner.stop();
      }, error => this.spinner.stop());
  }


}
