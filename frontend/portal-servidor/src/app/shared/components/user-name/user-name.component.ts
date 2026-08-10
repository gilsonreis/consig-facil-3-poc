import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-user-name',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-name.component.html'
})
export class UserNameComponent {
  public authService = inject(AuthService);

  @Input() customClass: string = '';
}
