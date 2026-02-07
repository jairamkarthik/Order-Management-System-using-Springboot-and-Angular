import { Component } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {
  mode: 'login' | 'register' = 'login';
  loading = false;
  error: string | null = null;

  form = this.fb.group({
    username: ['admin', [Validators.required]],
    password: ['admin123', [Validators.required]],
    confirmPassword: ['admin123', [Validators.required]]
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) { }

  submit() {
    this.error = null;
    if (this.form.invalid) return;

    const { username, password, confirmPassword } = this.form.getRawValue();

    if (this.mode === 'register' && password !== confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    this.loading = true;

    const call$ = this.mode === 'register'
      ? this.auth.register(username!, password!)
      : this.auth.login(username!, password!);

    call$.subscribe({
      next: (res) => {
        this.auth.saveToken(res.token);
        this.router.navigateByUrl(this.auth.isAdmin() ? '/dashboard' : '/orders');
      },
      error: (e) => {
        this.error = e?.error?.message ?? 'Login failed';
        this.loading = false;
      },
      complete: () => this.loading = false
    });
  }
}
