import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';

@Injectable()
export class BaseService {

  public URL = "//localhost:8080";

  // public API_KEY = "583aea9c82cd59697a0aa7b1dc106a21";

  public _httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this._httpClient = httpClient;
  }

  public handleError(error: HttpErrorResponse) {
    if (error.status === 401) {
      location.reload(true);
    }
    return throwError(error);
  }
}
