import { Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, IonicModule]
})
export class LoginPage {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private AuthService: AuthService, private router: Router, private storageService: StorageService) {}

  ngOnInit() {
   this.storageService.clearToken();
  }

  login() {
    this.AuthService.login({ username: this.username, password: this.password }).subscribe({
      next: (res) => {
        console.log('✅ Token reçu :', res.token);
        this.storageService.saveToken(res.token);
        localStorage.setItem('token', res.token);
        this.router.navigateByUrl('/profil');
      },
      error: (err) => {
        console.error('❌ Erreur de connexion :', err);
        this.errorMessage = 'Identifiants incorrects ou serveur indisponible';
      }
    });
  }
}
