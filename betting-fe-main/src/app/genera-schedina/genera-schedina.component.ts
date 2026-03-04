import {Component, OnInit} from '@angular/core';
import {SchedinaService} from "../service/schedina.service";
import {Schedina} from "../domain/schedina";
import {BehaviorSubject} from "rxjs";
import {NgxUiLoaderService} from "ngx-ui-loader";

@Component({
  selector: 'app-genera-schedina',
  templateUrl: './genera-schedina.component.html',
  styleUrls: ['./genera-schedina.component.css']
})
export class GeneraSchedinaComponent implements OnInit {

  constructor(private service: SchedinaService,
              private spinner: NgxUiLoaderService) {
  }

  schedina: BehaviorSubject<Schedina[]> = new BehaviorSubject([]);

  vincita5: number
  vincita10: number;

  percentuale:number = 1;

  totaleQuota:number

  ngOnInit() {

    this.generaSchedina()
  }

  public generaSchedina(): void {
    this.spinner.start();
    this.service.getRandomSchedina()
      .subscribe(rs => {
        this.spinner.stop();
        this.schedina.next(rs);
        let totQuote = 1;
        rs.forEach(bet => {
          totQuote = totQuote * bet.quota;
          this.percentuale = this.percentuale + (100/bet.quota);
        });

        this.percentuale =(this.percentuale/rs.length);

        this.totaleQuota = totQuote;

      }, error1 => {
        this.spinner.stop()
      })
  }

}
