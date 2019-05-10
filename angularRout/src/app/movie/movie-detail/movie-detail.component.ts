import { PersonService } from './../../person/person.service';
import { MovieService } from '../movie.service';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Router} from '@angular/router';

@Component({
  selector: 'app-movie-detail',
  templateUrl: './movie-detail.component.html',
  styleUrls: ['./movie-detail.component.css']
})
export class MovieDetailComponent implements OnInit {

  constructor(private _activatedRoute: ActivatedRoute,
    private _movieService: MovieService, private _personService: PersonService, private _router: Router) { }
    
  movie: any = {}
  editMode: boolean = false
  ngOnInit() {
    this._activatedRoute.params.subscribe(params => {
      let id = params['id'];
      this._movieService.getById(id)
        .subscribe(response => {
          this.movie = response;
        })
    });
  }
  
  insertPerson(id:Number) {
    this._personService.getById(id).subscribe(
      response => {
        this.movie.cast.push(response)
      }
    )
  }

  updateMovie(params){
    this.movie.title = params.title
    this.movie.description = params.description
    this.movie.yearOfRelease = params.yearOfRelease
    this.movie.homeCountry = params.homeCountry
    this.movie.language = params.language
    this.movie.duration = params.duration
    this._movieService.updateMovie(this.movie).subscribe(
      response => {
        console.log(response)
      }
    )
    this.editMode = false
  }

  deleteMovie(id:Number) {
    this._movieService.deleteMovie(id).subscribe(
      response => {
        console.log(response)
      }
    )
    this._router.navigateByUrl('/movies')
  }

}
