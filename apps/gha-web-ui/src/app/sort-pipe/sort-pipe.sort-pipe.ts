import { Pipe, PipeTransform } from '@angular/core';
import { Repo } from '../model/repo.model';

@Pipe({ name: 'sortByUpdatedAt' })
export class SortPipe implements PipeTransform {
  transform(array: Array<Repo> | null): Array<Repo> {
    array = array || [];
    var orderedArray = array.slice().sort(function (a, b) {
      return a.updatedAt < b.updatedAt ? 1 : -1;
    });
    return orderedArray;
  }
}
