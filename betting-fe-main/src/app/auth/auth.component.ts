import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../service/auth.service";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {AuthModalComponent} from "../auth-modal/auth-modal.component";
import * as moment from "moment";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css']
})
export class AuthComponent implements OnInit {


  constructor(private authService: AuthService,
              private router: Router,
              private spinner: NgxUiLoaderService,
              private modalService: NgbModal) {
  }

  code: string;
  error: boolean;


  ngOnInit() {
  }

  login(): void {
    // this.spinner.start();
    this.authService
      .checkUser(this.code)
      .subscribe((rs) => {
        // this.spinner.stop();
        this.authService.storeCode(this.code);
        this.authService.storeExpiration(rs.dateExpiration);

        if (moment(this.authService.getExpireDate(), "DD/MM/YYYY").subtract(5, 'd').isBefore(moment())) {
          this.modalService.open(AuthModalComponent)
            .result.then(() => {
              this.router.navigate(['/pagamento'], {queryParams: {nuovoAbbonamento: false}});
              this.modalService.dismissAll();
            }, () => {
              this.router.navigate(['/home'])
              this.modalService.dismissAll();
            }
          );
        } else {
          this.router.navigate(['/home']);
        }
      }, (err) => {
        this.spinner.stop();
        this.error = true;
        console.log(err);
      })

  }
}
