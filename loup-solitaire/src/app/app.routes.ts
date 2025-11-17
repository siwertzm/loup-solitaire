import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/chapitre/chapitre.page').then(m => m.ChapitrePage),
  },
  {
    path: 'chapitre',
    loadComponent: () =>
      import('./pages/chapitre/chapitre.page').then(m => m.ChapitrePage),
  },
  {
    path: 'combat',
    loadComponent: () =>
      import('./pages/combat/combat.page').then(m => m.CombatPage),
  },
  {
    path: 'profil',
    loadComponent: () =>
      import('./pages/profil/profil.page').then(m => m.ProfilPage),
  },
];
