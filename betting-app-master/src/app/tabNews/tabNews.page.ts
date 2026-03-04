import {Component, OnInit} from '@angular/core';
import {FeedRssService} from './feed-rss.service';
import {FeedRssArticle, FeedRssSite} from '../models/feedRss';

@Component({
    selector: 'app-tabNews',
    templateUrl: 'tabNews.page.html',
    styleUrls: ['tabNews.page.scss'],
    providers: [FeedRssService]
})
export class TabNewsPage implements OnInit {
    sites: FeedRssSite[] = [];
    articles: FeedRssArticle[] = [];
    loaded = false;

    selectedId: number;

    constructor(
        private feedRssService: FeedRssService
    ) {
    }

    ngOnInit(): void {
        this.feedRssService.site().subscribe(
            (res) => {
                this.sites = res;
                this.fetchArticle(this.sites[0].id);
            },
            (err) => {
                console.error(err);
                // TODO messagemodal???
            }
        );
    }

    fetchArticle(id: number) {
        this.selectedId = id;
        this.loaded = false;
        this.feedRssService.fetchArticles(id).subscribe(
            (res) => {
                this.articles = res;
                this.loaded = true;
            },
            (err) => {
                console.error(err);
                // TODO messagemodal???
            }
        );
    }

}
