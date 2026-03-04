import {Component, HostListener, OnInit} from '@angular/core';

@Component({
  selector: 'app-page-template',
  templateUrl: './page-template.component.html',
  styleUrls: ['./page-template.component.css']
})
export class PageTemplateComponent implements OnInit {

  mobileView: boolean = true;

  constructor() { }

  ngOnInit() {
    this.checkMobileView(window.innerWidth);
  }

  private checkMobileView(innerWidth) {
    if(innerWidth > 800) {
      this.mobileView = false;
    } else {
      this.mobileView = true;
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    this.checkMobileView(event.target.innerWidth);
  }

}
