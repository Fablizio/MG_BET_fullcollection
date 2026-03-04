import {Component, OnDestroy, OnInit} from '@angular/core';
import {SidebarService} from '../service/sidebar.service';
import {BehaviorSubject, Subscription} from 'rxjs';
import {NgxUiLoaderService} from 'ngx-ui-loader';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-today-match',
  templateUrl: './today-match.component.html',
  styleUrls: ['./today-match.component.css']
})
export class TodayMatchComponent implements OnInit, OnDestroy {
  private subscription: Subscription;

  public listaOdds: BehaviorSubject<OddDTO[]> = new BehaviorSubject<OddDTO[]>([]);


  constructor(private oddService: SidebarService,
              private activatedRouter: ActivatedRoute,
              private spinner:NgxUiLoaderService) {
  }

  type = '';


  loadScript() {

    this.show = !this.show;

    const script = document.createElement('script');
    script.src = 'https://ls.soccersapi.com/widget/res/wo_w3804_6599a111925ef/widget.js';
    script.type = 'text/javascript';
    document.body.appendChild(script);
  }

  ngOnInit() {


    this.percentuale = undefined
    this.spinner.start();

    this.type =  this.activatedRouter.snapshot.params.type;


   if(this.type === 'smellBet'){

     this.subscription = this.oddService.getSmellBet().subscribe(rs => {

       this.listaOdds.next(rs);
       this.spinner.stop();
     },()=>this.spinner.stop());
   }else {
     this.subscription = this.oddService.getTodayMatch().subscribe(rs => {

       this.listaOdds.next(rs);
       this.spinner.stop();
     },()=>this.spinner.stop());

   }
  }

  percentuale: any;
  tipo: any='UNO';

  filter(){
    this.spinner.start();
    this.oddService.searchBy(this.percentuale,this.tipo).subscribe(rs=>{
      this.listaOdds.next(rs)
      this.spinner.stop();
    },error=>{
      this.spinner.stop()
    } )
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  direction = false;
  show: any = false;

  reverseList() {
    this.direction = !this.direction;
    this.listaOdds.next(this.listaOdds.getValue().reverse());
  }

}
