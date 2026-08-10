import { Component, OnInit, signal, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { UserNameComponent } from '../shared/components/user-name/user-name.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, UserNameComponent],
  templateUrl: './layout.component.html',
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class LayoutComponent implements OnInit {
  public authService = inject(AuthService);
  private router = inject(Router);

  isMobileMenuOpen = signal(false);

  ngOnInit() {
    if (this.authService.isLogado() && !this.authService.currentUser()) {
      this.authService.carregarPerfil();
    }
  }

  toggleMenu() {
    this.isMobileMenuOpen.update(v => !v);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
