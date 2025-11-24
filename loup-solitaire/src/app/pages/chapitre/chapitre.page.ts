import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { IonContent, IonButton } from "@ionic/angular/standalone";
import { forkJoin } from 'rxjs/internal/observable/forkJoin';
import { ChapitreService } from 'src/app/services/chapitres.services';
import { JoueurService } from 'src/app/services/joueur.services';
import { ObjetService } from 'src/app/services/objets.services';

@Component({
  selector: 'app-chapitre',
  templateUrl: './chapitre.page.html',
  styleUrls: ['./chapitre.page.scss'],
  imports: [IonContent],
})
export class ChapitrePage implements OnInit {
  chapitre: any = null;  
  joueur: any = null;
  liens: any = null;
  liftedIndex: number | null = null;
  showObjets = false;
  showEffets = false;
  atTop = true;
  atBottom = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private chapitreService: ChapitreService,
    private joueurService: JoueurService,
    private objetService: ObjetService
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const chapitreId = params['id'] ?? 0;
      this.loadChapitre(chapitreId);
    });
  }

  onScroll(event: any) {
    const scrollTop = event.target.scrollTop;
    const scrollHeight = event.target.scrollHeight;
    const clientHeight = event.target.clientHeight;
    this.atTop = scrollTop < 3;
    this.atBottom = scrollTop + clientHeight >= scrollHeight - 10;
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
      document.getElementById('takeAllBtn')?.setAttribute('style', 'display: none;');
      
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
        console.log(data);
        this.loadJoueur();
        this.loadObjetsPris(id);
        this.loadLiens();
        this.atTop = true;
        this.atBottom = false;
        const scroll = document.getElementById('texteScroll');
        if (scroll) scroll.scrollTop = 0;
      },
      error: (error) => {
        console.error('Error fetching chapitre:', error);
      }
    })
  }

  loadLiens() {
    this.chapitreService.getLiens().subscribe({
      next: (data) => {
        this.liens = data;
        console.log(data);
      },
      error: (error) => {
        console.error('Error fetching liens:', error);
      }
    })
  }

  loadJoueur() {
    this.joueurService.getJoueurActif().subscribe({
      next: (data) => {
        this.joueur = data;
        console.log(data);
      },
      error: (error) => {
        console.error('Error fetching joueur:', error);
      }
    }) 
  }

  navigateTo(id: number) {
    this.showObjets = false;
    this.showEffets = false;
    document.getElementById('liens')?.setAttribute('style', 'display: flex;');
    document.getElementById('takeAllBtn')?.setAttribute('style', 'display: none;');
    this.router.navigate(['/chapitre'], {
      queryParams: { id }
    });
    console.log('Navigating to chapitre with id:', id);
  }

  utiliserObjet(objet: any) {
    if (!objet.optionnel) return; // objet optionnel, ne rien faire
    if (objet.valeur <= 0) return; // objet déjà épuisé
    this.objetService.AjoutObjet(objet.objet.id).subscribe({
      next: (joueurMaj) => {
        this.joueur = joueurMaj;
        objet.valeur--;
        this.chapitre.objet = [...this.chapitre.objet];
      },
      error: (err) => {
        console.error('Erreur ajout objet :', err);
      }
    });
  }

  takeAll() {
     if (!this.chapitre?.objet) return;
     const calls = [];
     for (let objWrap of this.chapitre.objet) {
        if (objWrap.optionnel) continue; // objet non ramassable
        if (objWrap.valeur <= 0) continue; // objet déjà ramassé

        const restant = objWrap.valeur;
        for (let i = 0; i < restant; i++) {
          calls.push(this.objetService.AjoutObjet(objWrap.objet.id));
        }
      }

      forkJoin(calls).subscribe({
        next: (results) => {
          // Mettre à jour le joueur avec la dernière réponse
          if (results.length > 0) {
            this.joueur = results[results.length - 1];

            // Griser tous les objets
            for (let objWrap of this.chapitre.objet) {
              if (objWrap.optionnel) {
                objWrap.valeur = 0;
                objWrap.optionnel = false;
              }
            }
            this.chapitre.objet = [...this.chapitre.objet];
          }
        },
          error: (err) => console.error(err)
        });
  }

  loadObjetsPris(chapitreId: number) {
    this.objetService.getObjetsPris(chapitreId).subscribe({
      next: (prises) => {

        // Chaque objetPris renvoie { objetId, quantite }
        for (let objWrap of this.chapitre.objet) {

          const prise = prises.find(p => p.objetId === objWrap.objet.id);

          if (prise) {
            // Si déjà ramassé autant que la quantité du chapitre → griser
            const restant = objWrap.valeur - prise.quantite;
            // si plus rien → griser
            if (restant <= 0) {
              objWrap.valeur = 0;
              objWrap.optionnel = false;
            } else {
              objWrap.valeur = restant;

          }
        }
      }
        // 🌟 Forcer Angular à mettre à jour le DOM
        this.chapitre.objet = [...this.chapitre.objet];
      }
    });
  }
}