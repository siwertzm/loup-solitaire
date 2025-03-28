import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  IonContent,
  IonHeader,
  IonTitle,
  IonToolbar,
  IonList,
  IonListHeader,
  IonItem,
  IonLabel,
  IonInput,
  IonButton
} from '@ionic/angular/standalone';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-profil',
  templateUrl: './profil.page.html',
  styleUrls: ['./profil.page.scss'],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    IonContent,
    IonHeader,
    IonTitle,
    IonToolbar,
    IonList,
    IonListHeader,
    IonItem,
    IonLabel,
    IonInput,
    IonButton
  ]})
export class ProfilPage implements OnInit {
  user: any;
  joueurs: any[] = [];
  nouveauNom: string = '';

  constructor(private authservice: AuthService) { }

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile(){
    this.authservice.me().subscribe({
      next: (res) => {
        this.user = res;
        this.joueurs = res.joueurs;
        console.log('Profil utilisateur chargé', res);
        console.log('Joueurs associés', this.joueurs);
      },
      error: (err) => {
        console.error('Erreur lors du chargement du profil utilisateur', err);
      }
    });
  }

  creerNouveauJoueur() {
    const joueur = { nom: this.nouveauNom };

    this.authservice.createJoueur(joueur).subscribe({
      next: (joueurCree) => {
        this.joueurs.push(joueurCree);
        this.nouveauNom = '';
      },
      error: (err) => {
        console.error('Erreur création joueur', err);
      }
    });
  }
}
