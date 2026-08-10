import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioPerfil } from '../models/usuario-perfil.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly TOKEN_KEY = 'portal_servidor_token';

  currentUser = signal<UsuarioPerfil | null>(null);

  login(identificador: string, senha: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>('/api/v1/auth/login', { identificador, senha });
  }

  salvarToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLogado(): boolean {
    return !!this.getToken();
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.currentUser.set(null);
  }

  carregarPerfil(): Observable<UsuarioPerfil> {
    const obs = this.http.get<UsuarioPerfil>('/api/v1/auth/me');
    obs.subscribe({
      next: (perfil) => {
        this.currentUser.set(perfil);
      },
      error: (err) => {
        console.error('Erro ao carregar perfil do usuário:', err);
        this.logout();
      }
    });
    return obs;
  }
}
