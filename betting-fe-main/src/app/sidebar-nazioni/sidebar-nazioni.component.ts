import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {SidebarService} from '../service/sidebar.service';
import {AuthService} from '../service/auth.service';
import {Router} from '@angular/router';
import {FeedRssService} from '../service/feed-rss.service';
import {BehaviorSubject} from 'rxjs';
import {Campionato} from '../domain/campionato';
import {UrlFeedRss} from '../domain/url-feed-rss';
import {animate, state, style, transition, trigger} from '@angular/animations';

@Component({
  selector: 'app-sidebar-nazioni',
  templateUrl: './sidebar-nazioni.component.html',
  styleUrls: ['./sidebar-nazioni.component.css'],
  animations: [
    trigger(
      'enterAnimation', [
        transition(':enter', [
          style({transform: 'translateX(-220px)'}),
          animate('250ms', style({transform: 'translateX(0)'}))
        ]),
        transition(':leave', [
          style({transform: 'translateX(0)'}),
          animate('250ms', style({transform: 'translateX(-220px)'}))
        ])
      ]
    ),
    trigger('openClose', [
      state('true', style({ transform: 'translateX(220px)' })),
      state('false', style({ transform: 'translateX(0)' })),
      transition('false <=> true', animate(250))
    ])
  ],
})
export class SidebarNazioniComponent implements OnInit,OnDestroy {

  _mobileView: boolean = true;
  isOpen: boolean = false;

  constructor(public sidebarservice: SidebarService,
              private authService:AuthService,
              private router :Router,
              private feedService:FeedRssService) {
  }

  campionati:BehaviorSubject<Campionato[]> = new BehaviorSubject([]);
  listUrl:BehaviorSubject<UrlFeedRss[]> = new BehaviorSubject([]);

  showMenuNazioni = true;
  showMenuHistory = true;
  showNotizie = true;
  showHistory = true;

  @Input("mobileView")
  set mobileView(value) {
    this._mobileView = value;
    if(value) {
      this.isOpen = false;
    } else {
      this.isOpen = true;
    }
  }

  get mobileView() {
    return this._mobileView;
  }

  ngOnInit() {
    this.sidebarservice.getCampionati()
      .subscribe(campionato => {
        this.campionati.next(campionato);
      });

    this.feedService.getUrlFeedRSS()
      .subscribe(rs=>{
        this.listUrl.next(rs);
      })
  }

  clickOnLink() {
    if(this._mobileView)
      this.isOpen = false;
  }

  ngOnDestroy(): void {

    if(this.campionati)
      this.campionati.unsubscribe()

    if(this.listUrl)
      this.listUrl.unsubscribe()
  }

}
