package com.loupsolitaire.backend.service;
import org.springframework.stereotype.Service;

import com.loupsolitaire.backend.model.Combat;
import com.loupsolitaire.backend.model.Ennemi;
import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.CombatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CombatService {

    private final CombatRepository combatRepository;
    private final JoueurService joueurService;

    private static final int[][] TABLE_LS = {
    { -6, 0,  0,  0,  0, -1, -2, -3, -4, -5 }, // col 0 : -11 ou +
    { -7, 0,  0,  0, -1, -2, -3, -4, -5, -6 }, // col 1 : -10/-9
    { -8, 0,  0, -1, -2, -3, -4, -5, -6, -7 }, // col 2 : -8/-7
    { -9, 0, -1, -2, -3, -4, -5, -6, -7, -8 }, // col 3 : -6/-5
    { -10, -1, -2, -3, -4, -5, -6, -7, -8, -9 }, // col 4 : -4/-3
    { -11, -2, -3, -4, -5, -6, -7, -8, -9, -10 }, // col 5 : -2/-1
    { -12, -3, -4, -5, -6, -7, -8, -9, -10, -11 }, // col 6 : 0
    { -14, -4, -5, -6, -7, -8, -9, -10, -11, -12 }, // col 7 : 1/2
    { -16, -5, -6, -7, -8, -9, -10, -11, -12, -14 }, // col 8 : 3/4
    { -18, -6, -7, -8, -9, -10, -11, -12, -14, -16 }, // col 9 : 5/6
    { -999, -7, -8, -9, -10, -11, -12, -14, -16, -18 }, // col 10 : 7/8
    { -999, -8, -9, -10, -11, -12, -14, -16, -18, -999 }, // col 11 : 9/10
    { -999, -9, -10, -11, -12, -14, -16, -18, -999, -999 }  // col 12 : 11 ou +
};

    private static final int[][] TABLE_E = {
    { -999, 0,  -3,  -4,  -5,  -6,  -7,  -8,  -9, -999 }, // col 0 : -11 ou +
    { -999, 0,  -3,  -4,  -5,  -6,  -6,  -7,  -7,  -10 }, // col 1 : -10/-9
    { -10, 0,  -2,  -3,  -4,  -5,  -5,  -6,  -6,  -8 }, // col 2 : -8/-7
    { -8, 0,  -1,  -2,  -3,  -4,  -4,  -5,  -5,  -6 }, // col 3 : -6/-5
    { -7, 0,  -1,  -1,  -2,  -3,  -4,  -4,  -5,  -5 }, // col 4 : -4/-3
    { -6, 0,  -1,  -1,  -2,  -2,  -3,  -4,  -4,  -5 }, // col 5 : -2/-1
    { -5, 0,   0,  -1,  -1,  -2,  -2,  -3,  -4,  -4 }, // col 6 : 0
    { -5, 0,   0,  -1,  -1,  -2,  -2,  -3,  -3,  -4 }, // col 7 : 1/2
    { -4, 0,   0,  -1,  -1,  -2,  -2,  -2,  -3,  -3 }, // col 8 : 3/4
    { -4, 0,   0,   0,  -1,  -1,  -2,  -2,  -3,  -3 }, // col 9 : 5/6
    { -4, 0,   0,   0,   0,  -1,  -2,  -2,  -2,  -3 }, // col 10 : 7/8
    { -3, 0,   0,   0,   0,  -1,  -2,  -2,  -2,  -3 }, // col 11 : 9/10
    { -3, 0,   0,   0,   0,  -1,  -1,  -2,  -2,  -2 }, // col 12 : 11 ou +
};

    // TABLE DÉFENSE : réduction en %
    private static final int[] REDUCTION_DEFENSE = {100,10,20,30,40,50,60,70,75,80};

    // TABLE DÉFENSE : bonus d’habilité
    private static final int[] BONUS_HABILITE = {0,0,0,1,1,2,2,2,3,3};
    
    //=============================================================
    //FONCTIONS PRINCIPALES
    //=============================================================

    public Combat demarrerCombat(Joueur joueur, Ennemi ennemi) {

        // Implémentation de la logique pour démarrer un combat
        Combat combat = new Combat();
        combat.setTourActuel(1);
        combat.setReductionDegat(0);
        combat.setBonusHabilite(0);
        combat.setCombatTermine(false);
        combat.setJoueur(joueur);
        combat.setEnnemi(ennemi);
        combat.setHasard(0);

        return combatRepository.save(combat);
    }

    public Combat actionAttaque(Long combatId) {
        Combat combat = loadCombat(combatId);
        if (combat.isCombatTermine()) return combat;

        attaquerJoueur(combat);
        if (!combat.isCombatTermine()) {
            attaquerEnnemi(combat);
        }

        combat.setTourActuel(combat.getTourActuel() + 1);
        return combatRepository.save(combat);
    }

    public Combat actionDefendre(Long combatId) {
        Combat combat = loadCombat(combatId);
        if (combat.isCombatTermine()) return combat;

        defenseJoueur(combat);
        attaquerEnnemi(combat);

        combat.setTourActuel(combat.getTourActuel() + 1);
        return combatRepository.save(combat);
    }

    public Combat actionObjet(Long combatId, Objet objet) {
        Combat combat = loadCombat(combatId);
        if (combat.isCombatTermine()) return combat;

        utiliserObjet(combat, objet);
        attaquerEnnemi(combat);

        combat.setTourActuel(combat.getTourActuel() + 1);
        return combatRepository.save(combat);
    }

    //=============================================================
    //UTILITAIRES INTERNES
    //=============================

    public Combat loadCombat(Long id) {
        return combatRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Combat non trouvé : " + id));
    }

    private int diffToColumn(int diff) {
        if (diff <= -11) return 0;
        if (diff <= -9) return 1;
        if (diff <= -7) return 2;
        if (diff <= -5) return 3;
        if (diff <= -3) return 4;
        if (diff <= -1) return 5;
        if (diff == 0) return 6;
        if (diff <= 2) return 7;
        if (diff <= 4) return 8;
        if (diff <= 6) return 9;
        if (diff <= 8) return 10;
        if (diff <= 10) return 11;
        return 12;
    }

    private int tirageHasard() {
        return (int)(Math.random() * 10);
    }

    //=============================================================
    //Attaque et Défense
    //=============================================================

    private void attaquerJoueur(Combat combat) {
        if (combat.isCombatTermine()) return;
        Joueur j = combat.getJoueur();
        Ennemi e = combat.getEnnemi();

        int habJ = j.getHabilite() + j.getHabiliteTemporaire() + combat.getBonusHabilite();
        int habE = e.getHabilite();
        int diff = habJ - habE;

        int col = diffToColumn(diff);
        int hasard = tirageHasard();
        combat.setHasard(hasard);

        int degats = TABLE_LS[col][hasard];

        e.setEndurance(e.getEndurance() + degats);
        combat.setBonusHabilite(0);

        if (e.getEndurance() <= 0) {
            combat.setCombatTermine(true);
        }
    }

    private void attaquerEnnemi(Combat combat) {
        if (combat.isCombatTermine()) return;
        Joueur j = combat.getJoueur();
        Ennemi e = combat.getEnnemi();

        int habJ = j.getHabilite() + j.getHabiliteTemporaire();
        int habE = e.getHabilite();
        int diff = habJ - habE;

        int col = diffToColumn(diff);
        int hasard = tirageHasard();
        combat.setHasard(hasard);

        int degatsBase = TABLE_E[col][hasard];

        // Appliquer réduction de dégâts si en défense
        int reduction = combat.getReductionDegat();
        if (reduction > 0) {
            double d = degatsBase * (100.0 - reduction) / 100.0;
            degatsBase = (int) Math.floor(d);
        }

        j.setEndurance(j.getEndurance() + degatsBase);

        // reset défense
        combat.setReductionDegat(0);

        if (j.getEndurance() <= 0) {
            combat.setCombatTermine(true);
        }
    }

    private void defenseJoueur(Combat combat) {
        int hasard = tirageHasard();
        combat.setHasard(hasard);
        combat.setReductionDegat(REDUCTION_DEFENSE[hasard]);
        combat.setBonusHabilite(BONUS_HABILITE[hasard]);
    }

    private void utiliserObjet(Combat combat, Objet objet) {
        joueurService.consomerObjet(combat.getJoueur(), objet);
    }

    
}
