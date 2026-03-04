import {Component, OnInit} from '@angular/core';
import {pluck, switchMap} from 'rxjs/operators';
import {ActivatedRoute} from '@angular/router';
import {FeedRssService} from '../service/feed-rss.service';
import {NgxUiLoaderService} from 'ngx-ui-loader';
import {BehaviorSubject} from 'rxjs';
import {FeedRss} from '../domain/feed-rss';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-feed-rss',
  templateUrl: './feed-rss.component.html',
  styleUrls: ['./feed-rss.component.css']
})
export class FeedRssComponent implements OnInit {

  constructor(private activatedRouter: ActivatedRoute,
              private feedService: FeedRssService,
              private spinner: NgxUiLoaderService,
              private toast: ToastrService) {
  }

  feeds: BehaviorSubject<FeedRss[]> = new BehaviorSubject([]);

  ngOnInit() {

    this.activatedRouter.params
      .pipe(
        pluck('idSite'),
        switchMap((id: string) => this.feedService.getNotizie(id, this.spinner))
      )
      .subscribe(notizie => {
        this.feeds.next(notizie);
        this.spinner.stop();
        if (notizie.length == 0)
          this.toast.error('Si è verificato un errore durante il reperimento delle notizie. Si prega di aggiornare la pagina', '', {
            timeOut: 10000
          })
      }, () => {
        this.spinner.stop();
      });

  }

}
