import {Component, OnInit} from '@angular/core';
import {SchedinaService} from "../service/schedina.service";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {BehaviorSubject} from "rxjs";
import {Raddoppio} from "../domain/raddoppio";

@Component({
  selector: 'app-history-raddoppi',
  templateUrl: './history-raddoppi.component.html',
  styleUrls: ['./history-raddoppi.component.css']
})
export class HistoryRaddoppiComponent implements OnInit {

  constructor(private schedinaService: SchedinaService,
              private spinner: NgxUiLoaderService) {
  }

  historyRaddoppio: BehaviorSubject<Raddoppio[]> = new BehaviorSubject([]);


  ngOnInit() {
    this.getHistoryRaddoppio()
  }

  public getHistoryRaddoppio() {
    this.spinner.start();

    this.schedinaService.getHistoryRaddoppio()
      .subscribe(rs => {
        this.spinner.stop();
        this.historyRaddoppio.next(rs);
      }, error1 => this.spinner.stop())
  }


}
