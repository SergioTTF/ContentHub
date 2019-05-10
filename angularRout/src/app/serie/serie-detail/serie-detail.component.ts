import { Component, OnInit } from '@angular/core';
import { SerieService } from '../serie.service';
import { ActivatedRoute } from '@angular/router';
import {Router} from '@angular/router';
import { PersonService } from './../../person/person.service';

@Component({
  selector: 'app-serie-detail',
  templateUrl: './serie-detail.component.html',
  styleUrls: ['./serie-detail.component.css']
})
export class SerieDetailComponent implements OnInit {

  constructor(private _activatedRoute: ActivatedRoute,
    private _serieService: SerieService, private _personService: PersonService, private _router: Router) { }
  editMode: boolean = false
  serie: any = {}
  ngOnInit() {
    this._activatedRoute.params.subscribe(params => {
      let id = params['id'];
      this._serieService.getById(id)
        .subscribe(response => {
          this.serie = response;
        })
    });
  }
  insertPerson(id:Number) {
    this._personService.getById(id).subscribe(
      response => {
        this.serie.cast.push(response)
      }
    )
  }

  updateSerie(params){
    console.log(params)
    this.serie.title = params.title
    this.serie.description = params.description
    this.serie.yearOfRelease = params.yearOfRelease
    this.serie.homeCountry = params.homeCountry
    this.serie.language = params.language
    this.serie.duration = params.duration
    this.serie.numberOfSeasons = params.numberOfSeasons
    this._serieService.updateSerie(this.serie).subscribe(
      response => {
        console.log(response)
      }
    )
    this.editMode = false
  }
  deleteSerie(id:Number) {
    this._serieService.deleteSerie(id).subscribe(
      response => {
        console.log(response)
      }
    )
    console.log(id)
    this._router.navigateByUrl('/series')
  }
  
}
