import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { appConfig } from './app/app.config';
import { authInterceptor } from './app/services/auth/auth.interceptor';
import {provideRouter} from "@angular/router";
import {routes } from './app/app.routes';

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes), //

    provideHttpClient(
      withInterceptors([authInterceptor]) // Registra el interceptor funcional aquí
    ),
  ],
}).catch((err) => console.error(err));
