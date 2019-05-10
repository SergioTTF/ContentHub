import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  { path: 'movies', loadChildren: './movie/movie.module#MovieModule'},
  { path: 'series', loadChildren: './serie/serie.module#SerieModule'},
  { path: 'people', loadChildren: './person/person.module#PersonModule'},
  { path: '', pathMatch: 'full', redirectTo: 'movies'},
  { path: '**', redirectTo: 'movies' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
