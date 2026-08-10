import { Component, OnInit, inject, signal, computed, effect, untracked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ServidorStateService } from '../../core/services/servidor-state.service';
import { ContratoService, SimulacaoResult } from '../../core/services/contrato.service';
import { FeedbackModalComponent } from '../../shared/components/feedback-modal/feedback-modal.component';

@Component({
  selector: 'app-simulacao',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FeedbackModalComponent],
  templateUrl: './simulacao.component.html'
})
export class SimulacaoComponent implements OnInit {
  private fb = inject(FormBuilder);
  public state = inject(ServidorStateService);
  private contratoService = inject(ContratoService);
  private router = inject(Router);

  form: FormGroup = this.fb.group({
    valorDesejado: [1000, [Validators.required, Validators.min(500), Validators.max(100000)]],
    quantidadeParcelas: [24, [Validators.required, Validators.min(1)]]
  });

  parcelasOpcoes = [12, 24, 36, 48, 72];
  
  resultadoSimulacao = signal<SimulacaoResult | null>(null);
  margemDisponivel = this.state.margemDisponivel;
  
  loadingMargem = this.state.loading;
  loadingSimulacao = signal<boolean>(false);
  isSolicitando = signal<boolean>(false);
  
  errorSimulacao = signal<string | null>(null);

  // Modal signals
  modalConfirmacaoAberto = signal<boolean>(false);
  modalSucessoAberto = signal<boolean>(false);

  // Expose signals
  servidores = this.state.servidores;
  matriculaAtiva = this.state.matriculaAtiva;

  // Validation if installment value exceeds available margin
  excedeMargem = computed(() => {
    const res = this.resultadoSimulacao();
    if (!res) {
      return false;
    }
    return res.valorParcela > this.margemDisponivel();
  });

  constructor() {
    // Listen to changes in the active matrícula to trigger simulation
    effect(() => {
      const active = this.matriculaAtiva();
      if (active) {
        untracked(() => {
          this.dispararSimulacao();
        });
      } else {
        this.resultadoSimulacao.set(null);
      }
    });

    // Listen to changes in the available margin (e.g. after load/update) to trigger simulation
    effect(() => {
      this.margemDisponivel();
      untracked(() => {
        this.dispararSimulacao();
      });
    });

    // Freeze inputs while soliciting
    effect(() => {
      const solicitando = this.isSolicitando();
      untracked(() => {
        if (solicitando) {
          this.form.disable();
        } else {
          this.form.enable();
        }
      });
    });
  }

  ngOnInit() {
    this.state.inicializarEstado();

    // Observe form changes to automatically simulate
    this.form.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b))
    ).subscribe(() => {
      this.dispararSimulacao();
    });
  }

  dispararSimulacao() {
    const active = this.matriculaAtiva();
    if (!active || this.form.invalid) {
      return;
    }

    this.loadingSimulacao.set(true);
    this.errorSimulacao.set(null);

    const payload = {
      servidorId: active.id,
      valorSolicitado: this.form.get('valorDesejado')?.value,
      taxaJurosMes: 2.15, // Taxa de juros padrão simulada
      quantidadeParcelas: this.form.get('quantidadeParcelas')?.value
    };

    this.contratoService.simular(payload).subscribe({
      next: (res) => {
        this.resultadoSimulacao.set(res);
        this.loadingSimulacao.set(false);
      },
      error: (err) => {
        console.error('Erro ao simular:', err);
        this.errorSimulacao.set('Ocorreu um erro ao calcular a simulação.');
        this.loadingSimulacao.set(false);
      }
    });
  }

  solicitarEmprestimo() {
    if (this.form.invalid || this.excedeMargem() || this.isSolicitando()) {
      return;
    }
    this.modalConfirmacaoAberto.set(true);
  }

  confirmarSolicitacao() {
    const active = this.matriculaAtiva();
    const valor = this.form.get('valorDesejado')?.value;
    const parcelas = this.form.get('quantidadeParcelas')?.value;

    if (!active || !valor || !parcelas || this.excedeMargem()) {
      return;
    }

    this.isSolicitando.set(true);
    this.errorSimulacao.set(null);

    const payload = {
      servidorId: active.id,
      valorSolicitado: valor,
      taxaJurosMes: 2.15,
      quantidadeParcelas: parcelas
    };

    this.contratoService.solicitar(payload).subscribe({
      next: (res) => {
        this.isSolicitando.set(false);
        this.modalConfirmacaoAberto.set(false);
        this.state.atualizarMargemDaMatriculaAtiva();
        this.modalSucessoAberto.set(true);
      },
      error: (err) => {
        console.error('Erro ao solicitar empréstimo:', err);
        if (err.error && err.error.message) {
          this.errorSimulacao.set(err.error.message);
        } else {
          this.errorSimulacao.set('Não foi possível efetivar a solicitação do empréstimo.');
        }
        this.isSolicitando.set(false);
        this.modalConfirmacaoAberto.set(false);
      }
    });
  }

  concluirSolicitacao() {
    this.modalSucessoAberto.set(false);
    this.router.navigate(['/dashboard']);
  }

  selectParcela(qtd: number) {
    if (this.isSolicitando()) {
      return;
    }
    this.form.patchValue({ quantidadeParcelas: qtd });
  }

  onSliderChange(event: Event) {
    if (this.isSolicitando()) {
      return;
    }
    const input = event.target as HTMLInputElement;
    const value = Number(input.value);
    this.form.patchValue({ valorDesejado: value });
  }

  onMatriculaChange(event: Event) {
    if (this.isSolicitando()) {
      return;
    }
    const select = event.target as HTMLSelectElement;
    const selectedId = Number(select.value);
    const selected = this.servidores().find(s => s.id === selectedId);
    if (selected) {
      this.state.setMatriculaAtiva(selected);
    }
  }
}
