import { Component } from '@angular/core';

@Component({
  selector: 'app-statium-landing',
  templateUrl: './statium-landing.component.html',
  styleUrls: ['./statium-landing.component.css']
})
export class StatiumLandingComponent {
  fundedPickUrl = 'https://www.thefundedpick.com/it';
  contactEmail = 'yellowprogram80@agentmail.to';
  lang: 'it' | 'en' = 'it';

  setLang(l: 'it' | 'en') {
    this.lang = l;
  }

  get isIt() {
    return this.lang === 'it';
  }
}
