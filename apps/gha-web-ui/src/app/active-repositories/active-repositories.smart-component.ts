import {Component, Input, OnInit} from '@angular/core';
import { ActiveRepositoriesService } from '../services/active-repositories.service';
import { Observable } from 'rxjs';
import { Repo } from '../model/repo.model';
import { PageEvent } from '@angular/material/paginator';

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
  startIndex = 0;
  endIndex = 10;

  constructor(private activeRepositoriesService: ActiveRepositoriesService) {}

  ngOnInit(): void {
    this.repos$ = this.activeRepositoriesService.repos$
    this.activeRepositoriesService.loadRepos(this.repoList, this.owner)
  }

  onPageChange(event: PageEvent) {
    this.startIndex = event.pageIndex * event.pageSize;
    this.endIndex = this.startIndex + event.pageSize;
  }
}
