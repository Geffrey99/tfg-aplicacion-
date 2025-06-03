import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable, PLATFORM_ID, Inject } from '@angular/core';

import { Observable, throwError, catchError, BehaviorSubject, tap } from 'rxjs';
import { usuario } from '../../interface/user';
import { LoginRequest } from '../../interface/loginRequest';
import { isPlatformBrowser } from '@angular/common';
import { CartService } from '../features/cart.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  currentUserLoginOn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  currentUserData: BehaviorSubject<usuario | null> = new BehaviorSubject<usuario | null>(null);

  private authToken: string | null = null;

  constructor(private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object, private cartSevice: CartService) {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage(): void {
    if (isPlatformBrowser(this.platformId)) {
      const user = localStorage.getItem('user');
      const token = localStorage.getItem('token');
      if (user && token) {
        this.setCurrentUser(JSON.parse(user), token);
      }
    }
  }

  login(credentials: LoginRequest): Observable<any> {
    return this.http.post<any>('https://hhreformas.es/login', credentials).pipe(
      tap((response) => {
        this.setCurrentUser(response.usuario, response.token);
        this.cartSevice.setCurrentUser(response.usuario);
      }),
      catchError(this.handleError)
    );
  }

  private setCurrentUser(userData: usuario, token: string): void {
    this.currentUserData.next(userData);
    this.currentUserLoginOn.next(true);
    this.authToken = token;
    if (isPlatformBrowser(this.platformId)) {
      localStorage.setItem('user', JSON.stringify(userData));
      localStorage.setItem('token', token);
    }
  }

  logout(): void {
    this.currentUserLoginOn.next(false);
    this.currentUserData.next(null);
    this.authToken = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  private getAuthHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authToken}`
    });
  }

  checkAuth(): boolean {
    return this.currentUserLoginOn.value;
  }

  private handleError(error: HttpErrorResponse) {
    if (error.status === 0) {
      console.error('Se ha producido un error:', error.error);
    } else {
      console.error(`Backend devolvió el código ${error.status}, cuerpo del error: ${error.error}`);
    }
    return throwError(() => new Error('Error de conexión'));
  }

  get userData(): Observable<usuario | null> {
    return this.currentUserData.asObservable();
  }

  get userLoginOn(): Observable<boolean> {
    return this.currentUserLoginOn.asObservable();
  }

  registerContacto(usuario: usuario): Observable<any> {
    console.log(usuario);
    return this.http.post<usuario>('https://hhreformas.es/api/usuario/register', usuario).pipe(
      tap((userData: usuario) => {
        this.currentUserData.next(userData);
        this.currentUserLoginOn.next(true);
      }),
      catchError(this.handleError)
    );
  }

  getUserProfile(): Observable<usuario> {
    return this.http.get<usuario>('https://hhreformas.es/api/usuario/profile', {
      headers: this.getAuthHeaders()
    });
  }
}
