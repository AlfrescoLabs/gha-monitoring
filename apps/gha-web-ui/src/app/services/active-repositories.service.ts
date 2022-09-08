import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { Repo } from '../model/repo.model';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class ActiveRepositoriesService {
  private baseUrl = 'https://api.github.com';
  private reposSource = new BehaviorSubject<any>([]);
  repos$:Observable<Repo[]> = this.reposSource.asObservable();
  private authorizationParam = '';
  private isLoggedInSource = new BehaviorSubject<boolean>(false);
  isLoggedIn$:Observable<boolean> = this.isLoggedInSource.asObservable();

  constructor(private http: HttpClient, private cookieService: CookieService) {}

  getRepo(repo:string, owner: string, baseUrl = this.baseUrl) {

    this.http.get(`${baseUrl}/repos/${owner}/${repo}/actions/runs`,
      {headers: {'Authorization': this.authorizationParam}})
      .pipe(
        map(res => this.formatRes(res))
      )
      .subscribe(workflows => {
        for (let workflow of workflows) {
          this.reposSource.next([...this.reposSource.getValue(), this.parseRepo((workflow))]);
        }
      })
  }

  private formatRes(res: any){
    return res["workflow_runs"]
  }

  private parseRepo(obj: any): Repo{
    var statusKey = obj.conclusion ?? obj.status ?? '';

    return {
      id: obj.id,
      status: statusKey.replace('_', ' '),
      conclusion: obj.conclusion ?? '',
      status_icon: this.getStatusIcon(statusKey),
      statusColor: this.getStatusColor(statusKey),
      updatedAt: obj.updated_at,
      repo: {
        name: obj.repository.name ?? '',
        url: obj.repository.html_url ?? '#',
      },
      headBranch: {
        name: obj.head_branch,
        url: `https://github.com/${obj.repository.owner.login}/${obj.repository.name}/tree/${obj.head_branch}` ?? '#',
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

  logOut(): void {
    this.isLoggedInSource.next(false);
    this.cookieService.delete('Token');
  }

  setToken(token: string): void {
    this.isLoggedInSource.next(true)
    this.cookieService.set('Token', token);
    this.cleanRepos()
  }

  checkCookies(): void {
    this.cookieService.get('Token') ? this.isLoggedInSource.next(true) : this.isLoggedInSource.next(false)
  }

  cleanRepos() {
    this.reposSource.next([])
  }

  loadRepos(repos: string[], owner: string){
    this.cookieService.get('Token') ? this.authorizationParam = `Bearer ${this.cookieService.get('Token')}` : this.authorizationParam = '';

    for (let repo of repos){
      this.getRepo(repo, owner);
    }
  }

  private getStatusColor(key: string): string{
    switch (key) {
      case "success":
        return 'green';
      case "failure":
      case "action_required":
      case "timed_out":
        return 'red';
      case "in_progress":
      case "queued":
      case "requested":
      case "waiting":
      case "completed":
        return 'goldenrod';
      case "cancelled":
      case "skipped":
      case "stale":
      case "neutral":
      case "startup_failure":
        return 'gray';
      default:
        return 'orange';
    }
  }

  private getStatusIcon(key: string): string{
    switch (key) {
      case "success":
        return 'check_circle';
      case "failure":
        return 'error';
      case "action_required":
        return 'account_circle';
      case "timed_out":
        return 'access_time';
      case "in_progress":
        return 'change_circle';
      case "queued":
      case "requested":
      case "waiting":
        return 'pending';
      case "cancelled":
        return 'cancel';
      case "skipped":
        return 'not_interested';
      case "stale":
        return 'history_toggle_off';
      case "neutral":
        return 'sentiment_neutral';
      case "startup_failure":
        return 'offline_bolt';
      case "completed":
      default:
        return 'circle';
    }
  }
}
