import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

interface AuthRequest {
  username: string;
  password: string;
}

interface AuthResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor( private http: HttpClient) { }

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, credentials);
  }

  me(): Observable<any> {
    return this.http.get(`${environment.apiUrl}/auth/me`);
  }

  createJoueur(joueur: any): Observable<any> {
    return this.http.post(`${environment.apiUrl}/joueurs`, joueur);
  }
}
