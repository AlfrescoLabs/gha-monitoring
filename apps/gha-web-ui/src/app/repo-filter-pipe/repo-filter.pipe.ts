import { Pipe, PipeTransform } from '@angular/core';
import { Repo } from '../model/repo.model';

@Pipe({
  name: 'repoFilter'
})
export class RepoFilterPipe implements PipeTransform {

  transform(repos: Array<Repo> | null, filter: string): Array<Repo> {
    if (!repos)
      return [];
    if (!filter)
      return repos;

    return repos.filter(repo => {
      return this.matchesAllPartsOfFilter(repo, filter.toLowerCase());
    });
  }

  private matchesAllPartsOfFilter(repo: Repo, filter: string): boolean {
    return filter.split(' ').every(part => this.matchesFilter(repo, part));
  }

  private matchesFilter(repo: Repo, filter: string): boolean {
    return repo.repo.name.toLowerCase().includes(filter) ||
      repo.headBranch.name.toLowerCase().includes(filter) ||
      repo.status.toLowerCase().includes(filter) ||
      repo.commit.id.toLowerCase().includes(filter);
  }

}
