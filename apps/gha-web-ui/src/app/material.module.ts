import { NgModule } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';

@NgModule({
  imports: [MatIconModule, MatInputModule, MatButtonModule, FormsModule],
  exports: [MatIconModule, MatInputModule, MatButtonModule, FormsModule]
})
export class MaterialModule {}
