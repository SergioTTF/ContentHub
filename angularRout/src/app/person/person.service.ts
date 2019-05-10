import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BaseService } from '../services/base/base.service';

@Injectable()
export class PersonService extends BaseService {

  constructor(httpClient: HttpClient) { 
    super(httpClient);
  }
  getDiscover() {
    return this._httpClient.get(`${this.URL}/people`)
  }

  updatePerson(person) {
    return this._httpClient.put(`${this.URL}/people?id=${person.id}`, person)
  }

  deletePerson(id:Number){
    return this._httpClient.delete(`${this.URL}/people/${id}`)
  }
  getSearch(text: string){
    return this._httpClient.get(`${this.URL}/people/filter/?name=${text}`)
  }

  getById(id: Number) {
    return this._httpClient.get(`${this.URL}/people/${id}`)
  }
}
