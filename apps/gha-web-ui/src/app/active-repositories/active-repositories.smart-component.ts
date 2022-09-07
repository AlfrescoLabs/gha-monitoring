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
  repos$: Observable<Repo[]>;
  private owner = 'Alfresco';
  private repoList: string[] = [
    'alfresco-community-repo',
    'alfresco-enterprise-repo',
    'alfresco-enterprise-share',
    'acs-packaging',
    'acs-community-packaging'
  ];

  constructor(private activeRepositoriesService: ActiveRepositoriesService) {}

  ngOnInit(): void {
    // this.repo$ = this.activeRepositoriesService.repo$
    this.repos$ = this.activeRepositoriesService.repos$
    console.log(this.repos$)
    // this.activeRepositoriesService.getRepo()
    this.activeRepositoriesService.loadRepos(this.repoList, this.owner)

  }
}
