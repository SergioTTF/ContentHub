import { Component } from '@angular/core';
import { delay } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Pitang DB';

  logged = false;
  collapsed = true;
  account: Account;

  constructor(private router: Router) { }

  ngOnInit() {
   
  }

  toggleCollapsed(): void {
    this.collapsed = !this.collapsed;
  }
}
