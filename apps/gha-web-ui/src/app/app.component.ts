import {Component, OnInit } from '@angular/core';
import { ActiveRepositoriesService } from './services/active-repositories.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.sass']
})
export class AppComponent implements OnInit{
  title = 'gha-web-ui';
  isLoggedIn$: Observable<boolean>;
  isLoading$: Observable<boolean>;

  owner = 'Alfresco';
  repoList: string[] = [
    'alfresco-community-repo',
    'alfresco-enterprise-repo',
    'alfresco-enterprise-share',
    'acs-packaging',
    'acs-community-packaging'
  ];

  constructor(private activeRepositoriesService: ActiveRepositoriesService) {
  }

  ngOnInit(){
    this.activeRepositoriesService.checkCookies();
    this.isLoggedIn$ = this.activeRepositoriesService.isLoggedIn$;
    this.isLoading$ = this.activeRepositoriesService.isLoading$;
  }

  testInput(val: any){
    this.activeRepositoriesService.setToken(val);
    this.activeRepositoriesService.loadRepos(this.repoList, this.owner);
  }

  deleteToken(){
    this.activeRepositoriesService.logOut();
    this.activeRepositoriesService.cleanRepos();
    this.activeRepositoriesService.loadRepos(this.repoList, this.owner);
  }
}
