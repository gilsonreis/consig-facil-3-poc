import { Component, OnInit, inject, signal, computed, effect, untracked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { ServidorStateService } from '../../core/services/servidor-state.service';
import { ContratoService } from '../../core/services/contrato.service';

export interface ParcelaMock {
  numero: number;
  valor: number;
  status: 'PAGO' | 'PENDENTE';
  dataVencimento: Date;
}

@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './extrato.component.html'
})
export class ExtratoComponent implements OnInit {
  public state = inject(ServidorStateService);
  private contratoService = inject(ContratoService);

  contratos = signal<any[]>([]);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  // Date filter
  dataFiltro = new FormControl('');
  dataSelecionada = signal<string>('');

  // Details Modal
  modalDetalhesAberto = signal<boolean>(false);
  contratoSelecionado = signal<any | null>(null);
  parcelas = signal<ParcelaMock[]>([]);

  // Expose signals
  servidores = this.state.servidores;
  matriculaAtiva = this.state.matriculaAtiva;

  // Computed signal to calculate total paid installments count
  pagasCount = computed(() => {
    return this.parcelas().filter(p => p.status === 'PAGO').length;
  });

  // Computed filter on list
  contratosFiltrados = computed(() => {
    const lista = this.contratos();
    const dataRef = this.dataSelecionada();
    if (!dataRef) {
      return lista;
    }

    const dataRefTime = new Date(dataRef + 'T00:00:00').getTime();

    return lista.filter(c => {
      if (!c.dataSolicitacao) {
        return false;
      }
      const dataContratoTime = new Date(c.dataSolicitacao).getTime();
      return dataContratoTime >= dataRefTime;
    });
  });

  constructor() {
    // Listen to changes in the active matrícula to reload contracts list
    effect(() => {
      const active = this.matriculaAtiva();
      if (active) {
        untracked(() => {
          this.carregarContratos(active.id);
        });
      } else {
        this.contratos.set([]);
      }
    });
  }

  ngOnInit() {
    this.state.inicializarEstado();

    // Listen to filter change and update signal
    this.dataFiltro.valueChanges.subscribe(val => {
      this.dataSelecionada.set(val || '');
    });
  }

  carregarContratos(matriculaId: number) {
    this.loading.set(true);
    this.error.set(null);
    this.contratoService.listarPorMatricula(matriculaId).subscribe({
      next: (data) => {
        this.contratos.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Erro ao buscar contratos:', err);
        this.error.set('Não foi possível carregar a lista de empréstimos.');
        this.loading.set(false);
      }
    });
  }

  abrirDetalhes(contrato: any) {
    this.contratoSelecionado.set(contrato);
    const qtdTotal = contrato.quantidadeParcelas;
    const pagas = Math.floor(Math.random() * (qtdTotal + 1));
    
    const parcelasGeradas: ParcelaMock[] = [];
    const baseDate = new Date(contrato.dataSolicitacao || new Date());

    for (let i = 1; i <= qtdTotal; i++) {
      const vctDate = new Date(baseDate);
      vctDate.setMonth(baseDate.getMonth() + i);

      parcelasGeradas.push({
        numero: i,
        valor: contrato.valorParcela || (contrato.valorTotal / qtdTotal),
        status: i <= pagas ? 'PAGO' : 'PENDENTE',
        dataVencimento: vctDate
      });
    }

    this.parcelas.set(parcelasGeradas);
    this.modalDetalhesAberto.set(true);
  }

  fecharDetalhes() {
    this.modalDetalhesAberto.set(false);
    this.contratoSelecionado.set(null);
    this.parcelas.set([]);
  }

  onMatriculaChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    const selectedId = Number(select.value);
    const selected = this.servidores().find(s => s.id === selectedId);
    if (selected) {
      this.state.setMatriculaAtiva(selected);
    }
  }

  limparFiltro() {
    this.dataFiltro.setValue('');
  }
}
