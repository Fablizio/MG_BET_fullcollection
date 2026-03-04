import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-pagamento',
  templateUrl: './pagamento.component.html',
  styleUrls: ['./pagamento.component.css']
})
export class PagamentoComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute) {

    this.activatedRoute
      .queryParams
      .subscribe(value => {
        this.iHaveACode = value.nuovoAbbonamento !== 'true';
      })

  }

  ngOnInit() {

  }

  iHaveACode: boolean;
  step = 1;


  goNext() {
    this.step++
  }

  goPrev() {
    this.step--

  }
}
