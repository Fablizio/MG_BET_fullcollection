import {Component, OnDestroy, OnInit, Output} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {BehaviorSubject, Subscription} from 'rxjs';
import {SidebarService} from '../service/sidebar.service';
import {pluck, switchMap} from 'rxjs/operators';
import {NgxUiLoaderService} from 'ngx-ui-loader';
import {ToastrService} from 'ngx-toastr';
import {League} from '../domain/league';

@Component({
  selector: 'app-table-prediction',
  templateUrl: './table-prediction.component.html',
  styleUrls: ['./table-prediction.component.css']
})
export class TablePredictionComponent implements OnInit, OnDestroy {

  @Output()
  listaOdds: BehaviorSubject<OddDTO[]> = new BehaviorSubject<OddDTO[]>([]);

  constructor(private activatedRouter: ActivatedRoute,
              public sidebarservice: SidebarService,
              private sidebarService: SidebarService,
              private spinner: NgxUiLoaderService,
              private toastr: ToastrService) {
  }

  campionato: any;
  leghe: BehaviorSubject<League[]> = new BehaviorSubject([]);

  idLegaSelected: string;
  private campionatiSubscription: Subscription;
  private oddsSubscription: Subscription;


  ngOnDestroy(): void {


    this.campionatiSubscription.unsubscribe();

    if (this.oddsSubscription)
      this.oddsSubscription.unsubscribe();

    if (this.leghe)
      this.leghe.unsubscribe();
  }


  ngOnInit() {


    this.activatedRouter
      .params
      .pipe(
        pluck('campionato')
      ).subscribe((campionatoFromUrl: string) => this.campionato = campionatoFromUrl);

    this.campionatiSubscription = this.sidebarservice.getCampionati()
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
              return this.sidebarService.getOdds(this.idLegaSelected, this.spinner);
            })
          )
          .subscribe(oddDto => {
            this.listaOdds.next(oddDto);
            this.spinner.stop();
            if (oddDto.length === 0) {
              this.toastr.warning('Prediction in elaborazione');
            } else {
              this.toastr.success('Prediction restituite');
            }
          }, () => this.spinner.stop());
      });
  }

  load() {
    this.oddsSubscription = this.sidebarService.getOdds(this.idLegaSelected, this.spinner)
      .subscribe(oddDto => {
        this.listaOdds.next(oddDto);
        this.spinner.stop();
        if (oddDto.length === 0) {
          this.toastr.warning('Prediction in elaborazione');
        } else {
          this.toastr.success('Prediction restituite');
        }
      }, () => this.spinner.stop());
  }

  direction = false;

  reverseList() {
    this.direction = !this.direction;
    this.listaOdds.next(this.listaOdds.getValue().reverse());
  }

}
