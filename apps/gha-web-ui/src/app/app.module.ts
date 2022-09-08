import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FlexLayoutModule } from '@angular/flex-layout';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { MatToolbarModule } from '@angular/material/toolbar';
import { HttpClientModule } from "@angular/common/http";

import { AppComponent } from './app.component';
import { ActiveRepositoriesSmartComponent } from './active-repositories/active-repositories.smart-component';
import { SortPipe } from './sort-pipe/sort-pipe.sort-pipe';
import { CookieService } from "ngx-cookie-service";

@NgModule({
  declarations: [
    AppComponent,
    ActiveRepositoriesSmartComponent,
    SortPipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    FlexLayoutModule,
    MatToolbarModule,
    HttpClientModule
  ],
  providers: [CookieService],
  bootstrap: [AppComponent]
})
export class AppModule { }
