import {Component, OnInit} from '@angular/core';
import {BettingService} from '../betting.service';
import {Evento} from '../models/betting';

@Component({
    selector: 'app-tabHome',
    templateUrl: 'tabHome.page.html',
    styleUrls: ['tabHome.page.scss'],
    providers: [BettingService]
})
export class TabHomePage implements OnInit {
    singleMatch: Evento[] = [];
    twoMatchs: Evento[] = [];
    doppiaChance: Evento[] = [];
    schedinaRandom: Evento[] = [];

    constructor(private bettingService: BettingService) {
    }

    ngOnInit(): void {
        this.fetchSingleMatch();
        this.fetchTwoMatchs();
        this.fetchDoppiaChance();
        this.fetchDoppiaChance();
        this.generaSchedina();
    }

    generaSchedina() {
        this.bettingService.generaSchedina().subscribe(
            (res) => this.schedinaRandom = res,
            (err) => {
                console.error(err);
                // TODO messagemodal???
            }
        );
    }

    private fetchSingleMatch() {
        this.bettingService.singleMatch().subscribe(
            (res) => this.singleMatch = res,
            (err) => {
                console.error(err);
                // TODO messagemodal???
            }
        );
    }

    private fetchTwoMatchs() {
        this.bettingService.twoMatchs().subscribe(
            (res) => this.twoMatchs = res,
            (err) => {
                console.error(err);
                // TODO messagemodal???
            }
        );
    }

    private fetchDoppiaChance() {
        this.bettingService.doppiaChance().subscribe(
            (res) => this.doppiaChance = res,
            (err) => {
                console.error(err);
                // TODO messagemodal???
            }
        );
    }

}
