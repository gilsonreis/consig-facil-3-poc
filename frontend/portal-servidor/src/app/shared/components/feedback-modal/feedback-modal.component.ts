import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-feedback-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './feedback-modal.component.html'
})
export class FeedbackModalComponent {
  @Input() isOpen = false;
  @Input() type: 'confirmacao' | 'sucesso' = 'confirmacao';
  @Input() title = '';
  @Input() message = '';

  @Output() onConfirm = new EventEmitter<void>();
  @Output() onCancel = new EventEmitter<void>();

  confirm() {
    this.onConfirm.emit();
  }

  cancel() {
    this.onCancel.emit();
  }
}
