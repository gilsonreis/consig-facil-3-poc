import { Injectable, inject, signal } from '@angular/core';
import { ServidorService } from './servidor.service';

@Injectable({
  providedIn: 'root'
})
export class ServidorStateService {
  private servidorService = inject(ServidorService);

  servidores = signal<any[]>([]);
  matriculaAtiva = signal<any | null>(null);
  margemDisponivel = signal<number>(0);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  inicializarEstado(): void {
    if (this.servidores().length > 0) {
      // Já inicializado, evita chamadas desnecessárias se já tiver dados
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    this.servidorService.getServidores().subscribe({
      next: (data) => {
        this.servidores.set(data);
        if (data && data.length > 0) {
          this.matriculaAtiva.set(data[0]);
          this.atualizarMargemDaMatriculaAtiva();
        } else {
          this.matriculaAtiva.set(null);
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar servidores:', err);
        this.error.set('Não foi possível carregar as matrículas.');
        this.loading.set(false);
      }
    });
  }

  setMatriculaAtiva(matricula: any): void {
    this.matriculaAtiva.set(matricula);
    this.atualizarMargemDaMatriculaAtiva();
  }

  atualizarMargemDaMatriculaAtiva(): void {
    const active = this.matriculaAtiva();
    if (!active) {
      this.margemDisponivel.set(0);
      return;
    }

    this.servidorService.getMargem(active.id).subscribe({
      next: (res) => {
        this.margemDisponivel.set(res.margemDisponivel);
      },
      error: (err) => {
        console.error('Erro ao atualizar margem:', err);
      }
    });
  }
}
