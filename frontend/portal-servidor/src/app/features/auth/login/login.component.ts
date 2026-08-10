import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup = this.fb.group({
    cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.?\d{3}\.?\d{3}-?\d{2}$|^\d{11}$/)]],
    senha: ['', [Validators.required, Validators.minLength(4)]]
  });

  loading = false;
  errorMessage = '';
  showPassword = false;

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    // Limpa pontos/traço do CPF antes de enviar para manter consistência
    const rawCpf = this.loginForm.value.cpf.replace(/\D/g, '');
    const senha = this.loginForm.value.senha;

    this.authService.login(rawCpf, senha).subscribe({
      next: (response) => {
        this.authService.salvarToken(response.token);
        this.authService.carregarPerfil();
        this.loading = false;
        // Navega para o dashboard após login bem sucedido
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 401) {
          this.errorMessage = 'CPF ou senha incorretos.';
        } else {
          this.errorMessage = 'Ocorreu um erro ao realizar o login. Tente novamente mais tarde.';
        }
      }
    });
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
