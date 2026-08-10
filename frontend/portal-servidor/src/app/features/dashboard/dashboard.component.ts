import { Component, OnInit, inject, signal, computed, effect, untracked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ServidorStateService } from '../../core/services/servidor-state.service';
import { ServidorService } from '../../core/services/servidor.service';
import { UserNameComponent } from '../../shared/components/user-name/user-name.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, UserNameComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  private state = inject(ServidorStateService);
  private servidorService = inject(ServidorService);

  margemResumo = signal<any | null>(null);
  contratos = signal<any[]>([]);
  
  loadingMargem = signal<boolean>(false);
  loadingContratos = signal<boolean>(false);
  
  errorMargem = signal<string | null>(null);
  errorContratos = signal<string | null>(null);

  // Expose state signals
  servidores = this.state.servidores;
  matriculaAtiva = this.state.matriculaAtiva;
  loadingServidores = this.state.loading;
  errorServidores = this.state.error;

  // Computed properties
  contratosAtivos = computed(() => {
    return this.contratos().filter(c => c.status === 'AVERBADO' || c.status === 'AGUARDANDO_AVERBACAO' || c.status === 'DIGITADO');
  });

  contratosAtivosCount = computed(() => {
    return this.contratosAtivos().length;
  });

  margemUtilizada = computed(() => {
    return this.contratosAtivos().reduce((sum, c) => {
      const valorTotal = c.valorTotal || 0;
      const qtdParcelas = c.quantidadeParcelas || 1;
      const valorParcela = valorTotal / qtdParcelas;
      return sum + (isNaN(valorParcela) ? 0 : valorParcela);
    }, 0);
  });

  // Overall loading state for cards
  isLoading = computed(() => {
    return this.loadingServidores() || this.loadingMargem() || this.loadingContratos();
  });

  constructor() {
    effect(() => {
      const activeMatricula = this.matriculaAtiva();
      if (activeMatricula) {
        untracked(() => {
          this.carregarDados(activeMatricula.id);
        });
      } else {
        this.margemResumo.set(null);
        this.contratos.set([]);
      }
    });
  }

  ngOnInit() {
    this.state.inicializarEstado();
  }

  carregarDados(servidorId: number) {
    this.loadingMargem.set(true);
    this.errorMargem.set(null);
    this.servidorService.getMargem(servidorId).subscribe({
      next: (data) => {
        this.margemResumo.set(data);
        this.loadingMargem.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar margem:', err);
        this.errorMargem.set('Não foi possível carregar a margem.');
        this.loadingMargem.set(false);
      }
    });

    this.loadingContratos.set(true);
    this.errorContratos.set(null);
    this.servidorService.getContratos(servidorId).subscribe({
      next: (data) => {
        this.contratos.set(data?.content || []);
        this.loadingContratos.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar contratos:', err);
        this.errorContratos.set('Não foi possível carregar os contratos.');
        this.loadingContratos.set(false);
      }
    });
  }

  onMatriculaChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    const selectedId = Number(select.value);
    const selected = this.servidores().find(s => s.id === selectedId);
    if (selected) {
      this.state.setMatriculaAtiva(selected);
    }
  }
}
