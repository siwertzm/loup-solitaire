import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ObjetService {

  private apiUrl = 'http://localhost:8080/objets';

  constructor(private http: HttpClient) {}

    AjoutObjet(objetId: number): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/ajouter/${objetId}`, {});
    }

    getObjetsPris(chapitreId: number) {
      return this.http.get<any[]>(`${this.apiUrl}/pris/${chapitreId}`);
  }
  
}