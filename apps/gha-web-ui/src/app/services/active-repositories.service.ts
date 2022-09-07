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

  private getStatusIcon(status: string, obj: any): string {
    if (status == 'completed') {
      // Can be one of: action_required, cancelled, failure, neutral, success, skipped, stale, timed_out
      if (obj.conclusion == 'success') {
        return 'check_circle';
      } else if (obj.conclusion == 'cancelled') {
        return 'cancel';
      } else if (obj.conclusion == 'failure') {
        return 'error';
      } else if (obj.conclusion == 'action_required') {
        return 'account_circle';
      } else if (obj.conclusion == 'neutral') {
        return 'sentiment_neutral';
      } else if (obj.conclusion == 'skipped') {
        return 'not_interested';
      } else if (obj.conclusion == 'stale') {
        return 'history_toggle_off';
      } else if (obj.conclusion == 'timed_out') {
        return 'access_time';
      }
    } else if (status == 'in_progress') {
      return 'change_circle';
    } else if (status == 'queued') {
      return 'pending';
    }
    // This shouldn't happen.
    return 'circle';
  }

  private parseRepo(obj: any): Repo{
    var status = obj.status ?? '';
    var status_icon = this.getStatusIcon(status, obj);

    return {
      id: obj.id,
      status: status,
      status_icon: status_icon,
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
