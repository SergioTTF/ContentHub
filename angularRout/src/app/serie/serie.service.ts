import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from '../services/base/base.service';

@Injectable()
export class SerieService extends BaseService {

  constructor(httpClient: HttpClient) {
    super(httpClient);
  }

  getDiscover() {
    return this._httpClient.get(`${this.URL}/series`)
  }

  getById(id: Number) {
    return this._httpClient.get(`${this.URL}/series/${id}`)
  }
  updateSerie(serie) {
    return this._httpClient.put(`${this.URL}/series?id=${serie.id}`, serie)
  }
  deleteSerie(id:Number){
    return this._httpClient.delete(`${this.URL}/series/${id}`)
  }
  getSearch(text: string){
    if(Number.isNaN(Number(text))){
      return this._httpClient.get(`${this.URL}/series/filter/?title=${text}&language=${text}`)
    } else {
      return this._httpClient.get(`${this.URL}/series/filter/?title=${text}&language=${text}&year=${Number(text)}`)
    }
    
  }
}