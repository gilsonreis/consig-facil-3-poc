import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServidorService {
  private http = inject(HttpClient);

  getServidores(): Observable<any[]> {
    return this.http.get<any[]>('/api/v1/servidores');
  }

  getMargem(id: number | string): Observable<any> {
    return this.http.get<any>(`/api/v1/servidores/${id}/margem`);
  }

  getContratos(id: number | string, page: number = 0, size: number = 100): Observable<any> {
    return this.http.get<any>(`/api/v1/servidores/${id}/contratos?page=${page}&size=${size}`);
  }
}
