import { Routes } from '@angular/router';
import { AuthGuard } from './services/auth.guard';

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
      canActivate: [AuthGuard]
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
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.page').then( m => m.LoginPage)
  },
  {
    path: 'register',
    loadComponent: () => import('./pages/register/register.page').then( m => m.RegisterPage)
  },
];
