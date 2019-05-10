import { PersonService } from './person.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-person',
  templateUrl: './person.component.html',
  styleUrls: ['./person.component.css']
})
export class PersonComponent implements OnInit {

  constructor(private _personService: PersonService, private _router: Router) { }
  people = []

  ngOnInit() {
    this._personService.getDiscover().subscribe(
      response => {
        this.people = response['content']
      }
    )
  }
  goEdit(person){
    this._router.navigate(['people/details', person.id])
  }

  searchPerson(text:string){
    this._personService.getSearch(text).subscribe(
      response => {
        this.people = response['content'];
      }
    )
  }

}
