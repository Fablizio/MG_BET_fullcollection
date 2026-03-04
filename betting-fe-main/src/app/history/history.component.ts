import {SidebarService} from "../service/sidebar.service";
import {Component, OnInit} from "@angular/core";
import {BehaviorSubject} from "rxjs";
import {ActivatedRoute} from "@angular/router";
import {League} from "../domain/league";
import {ToastrService} from "ngx-toastr";
import {pluck, switchMap} from "rxjs/operators";
import {HistoryService} from "../service/history.service";
import {NgxUiLoaderService} from "ngx-ui-loader";

@Component({
  selector: 'app-history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {

  constructor(private activatedRouter: ActivatedRoute,
              public sidebarservice: SidebarService,
              private historyService: HistoryService,
              private spinner: NgxUiLoaderService,
              private toastr: ToastrService) {
  }

  campionato: any;
  leghe: BehaviorSubject<League[]> = new BehaviorSubject([]);

  listaOdds: BehaviorSubject<OddDTO[]> = new BehaviorSubject<OddDTO[]>([]);

  idLegaSelected: string;

  ngOnInit() {

    this.activatedRouter.params.pipe(
      pluck('campionato'))
      .subscribe((campionatoFromUrl: string) => this.campionato = campionatoFromUrl);


    this.sidebarservice.getCampionati()
      .subscribe(campionati => {
        this.activatedRouter.params
          .pipe(
            pluck('territorio'),
            switchMap((territorio) => {
              this.campionato = territorio;
              const campionato = campionati.find(c => c.territorio === territorio);
              const leghe = campionato.sites;
              this.leghe.next(leghe);
              this.idLegaSelected = leghe[0].id;
              return this.historyService.getOdds(this.idLegaSelected, this.spinner);
            })
          )
          .subscribe(oddDto => {
            this.listaOdds.next(oddDto);
            this.spinner.stop();
            if (oddDto.length === 0) {
              this.toastr.warning('Non sono presenti History');
            } else {
              this.toastr.success('History presenti');
            }
          }, () => this.spinner.stop());
      });
  }

  load() {
    this.historyService.getOdds(this.idLegaSelected, this.spinner)
      .subscribe(oddDto => {
        this.listaOdds.next(oddDto);
        this.spinner.stop();
        if (oddDto.length === 0) {
          this.toastr.warning('Non sono presenti History');
        } else {
          this.toastr.success('History presenti');
        }
      }, () => this.spinner.stop());
  }

  direction = false;

  reverseList() {
    this.direction = !this.direction;
    this.listaOdds.next(this.listaOdds.getValue().reverse());
  }

}
