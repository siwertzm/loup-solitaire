import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonContent, IonButton } from "@ionic/angular/standalone";
import { ChapitreService } from 'src/app/services/chapitres.services';

@Component({
  selector: 'app-chapitre',
  templateUrl: './chapitre.page.html',
  styleUrls: ['./chapitre.page.scss'],
  imports: [IonContent],
})
export class ChapitrePage implements OnInit {
  chapitre: any = null;  
  endurance = 20;
  enduranceMax = 25;
  habilete = 15;
  liftedIndex: number | null = null;
  showObjets = false;
  showEffets = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private chapitreService: ChapitreService) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const chapitreId = params['id'] ?? 0;
      this.loadChapitre(chapitreId);
    });
  }

  lift(index: number) {
    this.liftedIndex = index;
    setTimeout(() => this.liftedIndex = null, 800);

    if (index === 0) {
      this.showObjets = false;
      this.showEffets = false;
      document.getElementById('liens')?.setAttribute('style', 'display: flex;');
      document.getElementById('takeAllBtn')?.setAttribute('style', 'display: none;');
    }
    if (index === 1) {
      this.showObjets = true;
      this.showEffets = false;
      document.getElementById('liens')?.setAttribute('style', 'display: none;');
      document.getElementById('takeAllBtn')?.setAttribute('style', 'display: flex;');
      
    }
    if (index === 2) {
      this.showEffets = true;
      this.showObjets = false;
      document.getElementById('liens')?.setAttribute('style', 'display: none;');
      document.getElementById('takeAllBtn')?.setAttribute('style', 'display: none;');
    }
  }

  loadChapitre(id: number) {
    this.chapitreService.getChapitre(id).subscribe({
      next: (data) => {
        this.chapitre = data;
        console.log(data)
        const scroll = document.getElementById('texteScroll');
        if (scroll) scroll.scrollTop = 0;
      },
      error: (error) => {
        console.error('Error fetching chapitre:', error);
      }
    })
  }

  navigateTo(id: number) {
    this.showObjets = false;
    this.showEffets = false;
    document.getElementById('liens')?.setAttribute('style', 'display: flex;');
    document.getElementById('takeAllBtn')?.setAttribute('style', 'display: none;');
    const target = event?.target as HTMLElement | null;
    target?.blur();
    this.router.navigate(['/chapitre'], {
      queryParams: { id }
    });
    console.log('Navigating to chapitre with id:', id);
  }

  utiliserObjet(objet: any) {
    if (!objet.optionnel) return; // objet optionnel, ne rien faire
    if (objet.valeur <= 0) return; // objet déjà épuisé

    objet.valeur--;

    this.chapitre = { ...this.chapitre };
  }

  takeAll() {
     if (!this.chapitre?.objet) return;
     for (let o of this.chapitre.objet) {
        if (o.optionnel) {
          o.valeur = 0;
        }
      }
      this.chapitre = { ...this.chapitre };
  }
}
