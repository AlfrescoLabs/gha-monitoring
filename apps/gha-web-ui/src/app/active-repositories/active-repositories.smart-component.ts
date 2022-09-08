import {Component, Input, OnInit} from '@angular/core';
import { ActiveRepositoriesService } from '../services/active-repositories.service';
import { Observable } from 'rxjs';
import { Repo } from '../model/repo.model';

@Component({
  selector: 'active-repositories',
  templateUrl: 'active-repositories.smart-component.html',
  styleUrls: ['active-repositories.smart-component.scss']
})
export class ActiveRepositoriesSmartComponent implements OnInit {

  @Input() repoList: string[]
  @Input() owner: string
  repos$: Observable<Repo[]>;
  filter: '';

  constructor(private activeRepositoriesService: ActiveRepositoriesService) {}

  ngOnInit(): void {
    this.repos$ = this.activeRepositoriesService.repos$
    this.activeRepositoriesService.loadRepos(this.repoList, this.owner)
  }
}
