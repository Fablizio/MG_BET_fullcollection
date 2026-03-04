import {Component, OnInit} from '@angular/core';
import {SiteService} from '../service/site.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-site',
  templateUrl: './site.component.html',
  styleUrls: ['./site.component.css']
})
export class SiteComponent implements OnInit {

  constructor(private siteService: SiteService,
              private toast: ToastrService) {
  }

  data: any[] = [];

  ngOnInit() {

    this.fetch();

  }

  selectedSite = null;
  selectedTerritory = null;
  doInsert: boolean = false;


  campionato = '';
  territorio = '';
  link = '';
  active = false;

  showDetails(site, territorio) {
    this.selectedSite = site;
    this.selectedTerritory = territorio;
  }

  deactivate() {
    this.siteService.enabled(false, this.selectedSite.id)
      .subscribe(value => {
        this.selectedSite.active = false;
        this.toast.success('Disattivato correttamente');
      }, error => {
        this.toast.error('Errore imprevisto ');
      });
  }

  activeLink() {
    this.siteService.enabled(true, this.selectedSite.id)
      .subscribe(value => {
        this.selectedSite.active = true;
        this.toast.success('Attivata correttamente');
      }, error => {
        this.toast.success('Si è verificato un errore.');
      });
  }

  fetch() {
    this.siteService.getAll().subscribe(value => {
      this.data = value;
    });
  }

  insert() {
    this.doInsert = true;
    this.selectedSite = null;
    this.selectedTerritory = null;
  }

  listaCampionati() {
    this.doInsert = false;
    this.fetch();
  }

  save() {
    this.siteService.save(this.territorio,this.campionato,this.link,this.active)
      .subscribe(value => {
        this.toast.success('Campionato inserito correttamente');
        this.territorio = '';
        this.campionato = '';
        this.link = '';
        this.active = false;
      }, error => {

        console.log(error.error.message)

        if(error.error.message === 'already-exist'){
          this.toast.error('Campionato esistente');
        }else{

          this.toast.error('Si è verificato un errore.');

        }
      });
  }

}
