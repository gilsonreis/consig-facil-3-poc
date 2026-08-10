import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

export interface SimulacaoResult {
  valorParcela: number;
  valorTotal: number;
  taxa: number;
  cet: number;
}

@Injectable({
  providedIn: 'root'
})
export class ContratoService {
  private http = inject(HttpClient);

  simular(payload: {
    servidorId: number;
    valorSolicitado: number;
    taxaJurosMes: number;
    quantidadeParcelas: number;
  }): Observable<SimulacaoResult> {
    return this.http.post<any>('/api/v1/contratos/simular', payload).pipe(
      map(res => ({
        valorParcela: res.valorParcela,
        valorTotal: res.valorTotal,
        taxa: payload.taxaJurosMes,
        cet: payload.taxaJurosMes * 1.12 // Custo Efetivo Total aproximado
      }))
    );
  }

  solicitar(payload: {
    servidorId: number;
    valorSolicitado: number;
    taxaJurosMes: number;
    quantidadeParcelas: number;
  }): Observable<any> {
    return this.http.post<any>('/api/v1/contratos/solicitar', payload);
  }

  listarPorMatricula(matriculaId: number): Observable<any[]> {
    return this.http.get<any>(`/api/v1/servidores/${matriculaId}/contratos`).pipe(
      map(res => res.content || [])
    );
  }
}
