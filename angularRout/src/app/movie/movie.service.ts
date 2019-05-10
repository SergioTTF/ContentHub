import { BaseService } from '../services/base/base.service';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class MovieService extends BaseService {

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }


  getDiscover() {
    return this._httpClient.get(`${this.URL}/movies`)
  }

  getById(id: Number) {
    return this._httpClient.get(`${this.URL}/movies/${id}`)
  }

  getSearch(text: string){
    if(Number.isNaN(Number(text))){
      return this._httpClient.get(`${this.URL}/movies/filter/?title=${text}&language=${text}`)
    } else {
      return this._httpClient.get(`${this.URL}/movies/filter/?title=${text}&language=${text}&year=${Number(text)}`)
    }
    
  }
  updateMovie(movie) {
    return this._httpClient.put(`${this.URL}/movies?id=${movie.id}`, movie)
  }
  deleteMovie(id:Number){
    return this._httpClient.delete(`${this.URL}/movies/${id}`)
  }
}
