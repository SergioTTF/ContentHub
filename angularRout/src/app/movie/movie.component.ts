import { MovieService } from './movie.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-movie',
  templateUrl: './movie.component.html',
  styleUrls: ['./movie.component.css']
})
export class MovieComponent implements OnInit {

  constructor(private _movieService: MovieService,
    private _router: Router) { }

  movies = [];

  ngOnInit() {
    this._movieService.getDiscover().subscribe(
      response => {
        this.movies = response['content'];
      }
    )
  }

  searchMovie(text:string){
    this._movieService.getSearch(text).subscribe(
      response => {
        this.movies = response['content'];
      }
    )
  }

  goEdit(movie) {

    this._router.navigate(['movies/details', movie.id])
  }

}
