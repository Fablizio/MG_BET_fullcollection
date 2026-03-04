import {Component, OnDestroy, OnInit} from '@angular/core';
import {SchedinaService} from "../service/schedina.service";
import {BehaviorSubject} from "rxjs";
import {Schedina} from "../domain/schedina";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {Raddoppio} from "../domain/raddoppio";
import {FeedRssService} from '../service/feed-rss.service';
import {LinkSocial} from '../domain/link-social';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit,OnDestroy {

  constructor(private schedinaService: SchedinaService,
              private spinner:NgxUiLoaderService,
              private feedService: FeedRssService) { }

  singleMatch: BehaviorSubject<Schedina[]> = new BehaviorSubject([]);
  twoMatch: BehaviorSubject<Schedina[]> = new BehaviorSubject([]);
  doppiaChance: BehaviorSubject<Schedina[]> = new BehaviorSubject([]);
  vincita15SingolMatch: number
  vincita15TwoMatch: number
  vincita15DoppiaChance: number
  totalQuoteTwoMatch:number
  totalQuoteDoppieChance:number;

  linkSocial:LinkSocial = new LinkSocial();

 async ngOnInit() {
    this.getSingleMatch();
    this.getTwoMatches();
    this.getDoppiaChance();
    this.getLink();
  }



  ngOnDestroy(): void {

  }

  public getLink(): void {
    this.spinner.start();
    this.feedService.getLinkSocial()
      .subscribe(rs => {

        this.spinner.stop();
        if(!rs.present){
          this.linkSocial.facebook = "https://www.facebook.com/ScomesseTips/";
          this.linkSocial.instagram = "https://www.instagram.com/mg_bet/?igshid=2s12mqtmv903";
        }else
          this.linkSocial = rs;
      }, error => {
        this.spinner.stop();
      })
  }


  public getSingleMatch(): void {
    this.spinner.start();
    this.schedinaService.getSingleMatch()
      .subscribe(rs => {
        this.spinner.stop();
        this.singleMatch.next(rs);
        let totQuote = 1;
        rs.forEach(bet => {
          totQuote = totQuote * bet.quota;
        });

        this.vincita15SingolMatch = totQuote * 15;
      }, error1 => {
        this.spinner.stop()
      })
  }

  public getTwoMatches(): void {
    this.spinner.start();
    this.schedinaService.getTwoMatches()
      .subscribe(rs => {
        this.spinner.stop();
        this.twoMatch.next(rs);
        let totQuote = 1;
        rs.forEach(bet => {
          totQuote = totQuote * bet.quota;
        });

        this.totalQuoteTwoMatch = totQuote;
      }, error1 => {
        this.spinner.stop()
      })
  }

  public getDoppiaChance(): void {
    this.spinner.start();
    this.schedinaService.getDoppiaChance()
      .subscribe(rs => {
        this.spinner.stop();
        this.doppiaChance.next(rs);
        let totQuote = 1;
        rs.forEach(bet => {
          totQuote = totQuote * bet.quota;
        });

        this.totalQuoteDoppieChance = totQuote;
      }, error1 => {
        this.spinner.stop()
      })
  }

}
