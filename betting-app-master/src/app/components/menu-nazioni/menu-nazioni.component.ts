import {Component, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Nazione, Site} from '../../models/betting';
import {BettingService} from '../../betting.service';
import {Events, IonContent} from '@ionic/angular';

export interface OnClickEvent {
    id: number;
    campionato: string;
}

@Component({
    selector: 'app-menu-nazioni',
    templateUrl: 'menu-nazioni.component.html',
    styleUrls: ['menu-nazioni.component.scss']
})
export class MenuNazioniComponent implements OnInit, OnDestroy {
    @Output()
    onClick: EventEmitter<OnClickEvent> = new EventEmitter();
    @Output()
    onClose: EventEmitter<any> = new EventEmitter();

    selectedId: number;
    nations: Nazione[] = [];
    sites$;

    // @ViewChild(IonContent, {static: false}) content: IonContent;

    constructor(
        private bettingService: BettingService,
        private events: Events
    ) {
    }

    ngOnInit(): void {
        this.sites$ = this.bettingService.sites.subscribe((nazioni) => {
            this.nations = nazioni;
            this.nations[0].open = true;
            this.selectSite(this.nations[0].sites[0]);
        });
        this.events.subscribe('sidemenu:toogle', (visible) => {
            if (visible) {
                setTimeout(() => this.scrollTo(), 100);
            }
        });
    }

    ngOnDestroy(): void {
        this.sites$.unsubscribe();
    }

    selectSite(site: Site) {
        this.selectedId = site.id;
        this.onClick.emit({
            id: site.id,
            campionato: site.campionato
        });
        this.onClose.emit();
    }

    scrollTo() {
        // console.log(this.selectedId.toString());
        // const active = document.getElementById(this.selectedId.toString());
        // if (active && active.offsetParent) {
        //     console.log((active.offsetParent as any).offsetTop);
        //     this.content.scrollToPoint(0, (active.offsetParent as any).offsetTop - 50, 1);
        // }
    }

}
