import { PersonDetailComponent } from './person-detail/person-detail.component';
import { PersonRoutingModule } from './person.routing.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PersonService } from './person.service';
import { PersonComponent } from './person.component';

@NgModule({
  declarations: [
    PersonComponent,
    PersonDetailComponent
  ],
  imports: [
    CommonModule,
    PersonRoutingModule
  ],
  providers: [
    PersonService
  ]
})
export class PersonModule { }
