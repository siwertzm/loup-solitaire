import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChapitreService {

  private apiUrl = 'http://localhost:8080/chapitres';

  constructor(private http: HttpClient) {}

  getChapitre(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getLiens(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/lien`);
  }
}