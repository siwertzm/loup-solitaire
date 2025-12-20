import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { IonInput, IonButton, IonContent } from '@ionic/angular/standalone';

@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [IonInput, IonButton, FormsModule, IonContent]
})
export class LoginPage {
  username = "";
  password = "";

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  login() {
    console.log("LOGIN DATA:", this.username, this.password);
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigateByUrl('/profil', { replaceUrl: true }),
      error: () => alert("Username ou mot de passe incorrect")
    });
  }
  
  goToRegister() {
    this.router.navigate(['/register']);
  }
}