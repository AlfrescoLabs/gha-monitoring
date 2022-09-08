import { NgModule } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@NgModule({
  imports: [MatIconModule, MatInputModule, MatButtonModule, FormsModule, MatToolbarModule, MatProgressBarModule],
  exports: [MatIconModule, MatInputModule, MatButtonModule, FormsModule, MatToolbarModule, MatProgressBarModule]
})
export class MaterialModule {}
