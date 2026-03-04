import {Component, OnInit} from '@angular/core';
import {Odds} from '../models/betting';
import {BettingService} from '../betting.service';
import * as moment from 'moment';

@Component({
    selector: 'app-tabHistory',
    templateUrl: 'tabHistory.page.html',
    styleUrls: ['tabHistory.page.scss']
})
export class TabHistoryPage implements OnInit {
    odds: Odds[] = [];
    campionato: string;
    loaded = false;

    datepickerValue: string;
    loadSmellBet = false;

    constructor(
        private bettingService: BettingService
    ) {
    }

    fetchHistory(date?: any) {
        this.loadSmellBet = false;
        this.loaded = false;

        if (!date) {
            date = this.datepickerValue || moment().subtract(1, 'day');
        }

        date = moment(date).format('YYYY-MM-DD');

        this.bettingService.getHistoryTodayMatch(date)
            .subscribe((odds) => {
                this.campionato = moment(date).format('DD-MM-YYYY');
                this.odds = odds;
                this.loaded = true;
            });
    }

    loadByDatepicker() {

        if (this.loadSmellBet) {
            this.fetchHistorySmellBet()
        } else {
            this.fetchHistory();
        }


    }


    fetchHistorySmellBet(date?: any) {
        this.loaded = false;
        this.loadSmellBet = true;
        if (!date) {
            date = this.datepickerValue || moment().subtract(1, 'day');
        }

        date = moment(date).format('YYYY-MM-DD');

        this.bettingService.getHistorySmellBett(date)
            .subscribe((odds) => {
                this.campionato = moment(date).format('DD-MM-YYYY');
                this.odds = odds;
                this.loaded = true;
            });
    }

    ngOnInit(): void {

        const date = moment().subtract(1, 'day');
        this.fetchHistory(date);

    }

}
