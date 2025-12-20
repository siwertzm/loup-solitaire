import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { IonInput, IonButton, IonContent } from '@ionic/angular/standalone';

@Component({
  standalone: true,
  selector: 'app-register',
  templateUrl: './register.page.html',
  styleUrls: ['./register.page.scss'],
  imports: [IonInput, IonButton, FormsModule, IonContent],
})
export class RegisterPage {
  username = "";
  password = "";

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  register() {
    this.auth.register(this.username, this.password).subscribe({
      next: (res) => {
        console.log("REGISTER OK:", res);
        this.router.navigateByUrl('/login', { replaceUrl: true });
      },
      error: (err) => {
        console.log("REGISTER ERROR:", err);
        alert(err.error?.error || "Erreur lors de la création du compte");
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}