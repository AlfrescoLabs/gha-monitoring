import { NgModule } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatPaginatorModule } from '@angular/material/paginator';

@NgModule({
  imports: [MatIconModule, MatInputModule, MatButtonModule, FormsModule, MatToolbarModule, MatProgressBarModule, MatPaginatorModule],
  exports: [MatIconModule, MatInputModule, MatButtonModule, FormsModule, MatToolbarModule, MatProgressBarModule, MatPaginatorModule]
})
export class MaterialModule {}
