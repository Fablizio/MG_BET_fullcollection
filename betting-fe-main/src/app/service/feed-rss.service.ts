import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {UrlFeedRss} from "../domain/url-feed-rss";
import {environment} from "../../environments/environment";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {FeedRss} from "../domain/feed-rss";
import {LinkSocial} from '../domain/link-social';

@Injectable({
  providedIn: 'root'
})
export class FeedRssService {

  options = {
    headers: {
      CODE: localStorage.getItem('CODE')
    }
  };

  constructor(private httpClient:HttpClient) { }


  public getUrlFeedRSS():Observable<UrlFeedRss[]>{
    return this.httpClient.get<UrlFeedRss[]>(environment.BETTING_API+'feedRSS/site',this.options);
  }

  getNotizie(id: string, spinner: NgxUiLoaderService):Observable<FeedRss[]> {
    spinner.start()
    console.log(id)
    return this.httpClient.get<FeedRss[]>(environment.BETTING_API+'feedRSS/'+id,this.options);
  }

  public getLinkSocial(): Observable<LinkSocial> {
    return this.httpClient.get<LinkSocial>(environment.BETTING_API + 'betting/getLink',this.options)
  }
}
