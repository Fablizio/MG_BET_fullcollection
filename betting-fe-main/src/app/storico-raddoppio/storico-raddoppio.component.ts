import { Component, OnInit } from '@angular/core';
import {SchedinaService} from '../service/schedina.service';
import {BehaviorSubject} from 'rxjs';
import {Raddoppio} from '../domain/raddoppio';
import {NgxUiLoaderService} from 'ngx-ui-loader';

@Component({
  selector: 'app-storico-raddoppio',
  templateUrl: './storico-raddoppio.component.html',
  styleUrls: ['./storico-raddoppio.component.css']
})
export class StoricoRaddoppioComponent implements OnInit {

  constructor(private raddoppioService:SchedinaService,
              private spinner:NgxUiLoaderService) { }

  raddoppio:BehaviorSubject<Raddoppio[]> = new BehaviorSubject<Raddoppio[]>([]);

  oneMonth = false;
  threeMonth = false;
  sixMonth = false;

  ngOnInit() {
    this.search(1);
  }

  search(month){

    this.spinner.start();

    this.raddoppio.next([]);

    this.oneMonth = false;
    this.threeMonth = false;
    this.sixMonth = false;

    if(month === 1){
      this.oneMonth = true;
    }else if(month === 3){
      this. threeMonth = true;
    }else if(month === 6){
      this. sixMonth = true;
    }


    this.raddoppioService.getHistoryRaddoppioWithParams(this.oneMonth,this.threeMonth,this.sixMonth)
      .subscribe(rs=>{

        rs.forEach(r=>{
          r.quotaTotale = 1
          r.odds.forEach(odd=>{
            r.quotaTotale = r.quotaTotale * odd.quota;
          })
        })

        console.log(rs)

        this.raddoppio.next(rs);
        this.spinner.stop();
      },error => {
        this.spinner.stop();
      });
  }

}
