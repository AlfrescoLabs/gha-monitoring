import { Pipe, PipeTransform } from '@angular/core';
import { Repo } from '../model/repo.model';

@Pipe({ name: 'sortById' })
export class SortPipe implements PipeTransform {
  transform(array: Array<Repo> | null): Array<Repo> {
    array = array || [];
    var orderedArray = array.slice().sort(function (a, b) {
      return a.id - b.id;
    });
    return orderedArray;
  }
}
