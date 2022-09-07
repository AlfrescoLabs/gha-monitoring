import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { Repo } from '../model/repo.model';

@Injectable({
  providedIn: 'root'
})
export class ActiveRepositoriesService {
  private baseUrl = 'https://api.github.com';
  private repoSource = new BehaviorSubject<any>(null)
  repo$:Observable<Repo> = this.repoSource.asObservable()

  //for testing
  private owner = 'Alfresco';
  private repo = 'alfresco-community-repo';

  constructor(private http: HttpClient) {}

  getRepo(owner: string = this.owner, repo:string = this.repo, baseUrl = this.baseUrl){
    this.http.get(`${baseUrl}/repos/${owner}/${repo}/actions/runs`)
      .pipe(
        map(res => this.formatRes(res))
      )
      .subscribe(res => {
        this.repoSource.next(this.parseRepo(res[0]));
      })

  }

  private formatRes(res: any){
    return res["workflow_runs"]
  }

  private parseRepo(obj: any): Repo{
    return {
      id: obj.id,
      status: obj.status ?? '',
      repo: {
        name: obj.repository.name ?? '',
        url: obj.repository.html_url ?? '#',
      },
      commit: {
        id: obj.head_sha ?? '',
        url: `https://github.com/${obj.repository.owner.login}/${obj.repository.name}/commit/${obj.head_sha}` ?? '#',
      },
      run: {
        number: obj.run_number,
        url: obj.html_url ?? '#',
      },
      owner: {
        name: obj.repository.owner.login ?? '',
        url: obj.repository.owner.html_url ?? '#',
      }
    }
  }
}
