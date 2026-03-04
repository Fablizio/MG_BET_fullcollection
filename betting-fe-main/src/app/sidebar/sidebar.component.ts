import {Component, OnDestroy, OnInit} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AuthService} from "../service/auth.service";
import {Router} from "@angular/router";
import * as moment from "moment";

// import { MenusService } from './menus.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css'],
  animations: [
    trigger('slide', [
      state('up', style({height: 0})),
      state('down', style({height: '*'})),
      transition('up <=> down', animate(200))
    ])
  ]
})
export class SidebarComponent implements OnInit, OnDestroy {

  constructor(private authService: AuthService,
              private router: Router) {
  }


  ngOnInit() {


  }

  logout(): void {
    this.authService.logOut();
    this.router.navigateByUrl('').then();
  }

  ngOnDestroy(): void {

  }


  expire(): string {
    return this.authService.getExpireDate();
  }


}
