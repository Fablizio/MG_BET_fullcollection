import {Component} from '@angular/core';
import {animate, style, transition, trigger} from '@angular/animations';
import {Events} from '@ionic/angular';

@Component({
    selector: 'app-side-menu',
    templateUrl: 'side-menu.component.html',
    styleUrls: ['side-menu.component.scss'],
    animations: [
        trigger('slideInOut', [
            transition(':enter', [
                style({transform: 'translateX(+100%)'}),
                animate('200ms ease-in', style({transform: 'translateX(0%)'}))
            ]),
            transition(':leave', [
                animate('200ms ease-in', style({transform: 'translateX(+100%)'}))
            ])
        ])
    ]
})
export class SideMenuComponent {
    visible = false;

    constructor(private events: Events) {
    }

    close() {
        this.visible = false;
        this.events.publish('sidemenu:toogle', this.visible);
    }

    open() {
        this.visible = true;
        this.events.publish('sidemenu:toogle', this.visible);
    }
}
