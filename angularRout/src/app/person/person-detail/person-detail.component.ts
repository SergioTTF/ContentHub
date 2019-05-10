import { PersonService } from './../person.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-person-detail',
  templateUrl: './person-detail.component.html',
  styleUrls: ['./person-detail.component.css']
})
export class PersonDetailComponent implements OnInit {

  constructor(private _activatedRoute: ActivatedRoute,
     private _personService: PersonService, private _router: Router) { }
  person: any = {}
  editMode: boolean = false
  ngOnInit() {
    this._activatedRoute.params.subscribe(params => {
      let id = params['id'];
      this._personService.getById(id)
        .subscribe(response => {
          this.person = response;
        })
    });
  }
  gender(code:Number){
    if(code==1){
      return "Female"
    } else if(code==2){
      return "Male"
    }else{
      return "Other/Undefined"
    }
  }

  deleteMovie(id:Number) {
    this._personService.deletePerson(id).subscribe(
      response => {
        console.log(response)
      }
    )
    this._router.navigateByUrl('/people')
  }

}
