import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class JoueurService {

  private apiUrl = 'http://localhost:8080/joueurs';

  constructor(private http: HttpClient) {}

  getJoueurActif(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/actif`);
  }
}