import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonButton, IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';

@Component({
  selector: 'app-profil',
  templateUrl: './profil.page.html',
  styleUrls: ['./profil.page.scss'],
  standalone: true,
  imports: [IonContent, CommonModule, FormsModule, IonButton]
})
export class ProfilPage implements OnInit {

  constructor() { }
  user = null;
  joueurs = [];

  ngOnInit() {
    this.loadUser();
    this.loadJoueurs();
  }

  loadUser() {  }
  loadJoueurs() { }
  nouvelleAventure() {}
  supprimerCompte() {}

}
