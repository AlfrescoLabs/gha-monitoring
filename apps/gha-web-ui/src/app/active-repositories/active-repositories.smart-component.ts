import { Component, OnInit } from '@angular/core';
import { ActiveRepositoriesService } from '../services/active-repositories.service';
import { Observable } from 'rxjs';
import { Repo } from '../model/repo.model';

@Component({
  selector: 'active-repositories',
  templateUrl: 'active-repositories.smart-component.html',
  styleUrls: ['active-repositories.smart-component.scss']
})
export class ActiveRepositoriesSmartComponent implements OnInit {
  repo$: Observable<Repo>;

  constructor(private activeRepositoriesService: ActiveRepositoriesService) {}

  ngOnInit(): void {
    this.repo$ = this.activeRepositoriesService.repo$
    this.activeRepositoriesService.getRepo()
  }
}
