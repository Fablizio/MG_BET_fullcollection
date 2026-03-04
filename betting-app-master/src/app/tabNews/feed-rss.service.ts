import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {FeedRssArticle, FeedRssSite} from '../models/feedRss';
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class FeedRssService {

    constructor(private httpClient: HttpClient) {
    }

    site(): Observable<FeedRssSite[]> {
        return this.httpClient.get<FeedRssSite[]>(`${environment.endpoint}/feedRSS/site`);
    }

    fetchArticles(id: number): Observable<FeedRssArticle[]> {
        return this.httpClient.get<FeedRssArticle[]>(`${environment.endpoint}/feedRSS/${id}`);
    }
}

