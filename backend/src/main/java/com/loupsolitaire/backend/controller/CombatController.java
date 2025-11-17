package com.loupsolitaire.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loupsolitaire.backend.model.Combat;
import com.loupsolitaire.backend.model.Ennemi;
import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.EnnemiRepository;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.ObjetRepository;
import com.loupsolitaire.backend.service.CombatService;
import com.loupsolitaire.backend.service.JoueurActifService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/combats")
@RequiredArgsConstructor
public class CombatController {

    private final JoueurActifService joueurActifService;
    private final JoueurRepository joueurRepository;
    private final CombatService combatService;
    private final EnnemiRepository ennemiRepository;
    private final ObjetRepository objetRepository;

    //===========================================================
    // Demarer un combat
    //===========================================================

    @PostMapping("/start/{ennemiId}")
    public Combat startCombat(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String ennemiId) {
        Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
        if (joueurId == null) {
            throw new RuntimeException("Aucun joueur actif défini pour cet utilisateur");
        }
        Joueur joueur = joueurRepository.findById(joueurId)
            .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));

        Ennemi ennemi = ennemiRepository.findById(ennemiId)
            .orElseThrow(() -> new RuntimeException("Ennemi non trouvé : " + ennemiId));

        return combatService.demarrerCombat(joueur, ennemi);
    }

    // =============================================================
    // 2️⃣ ATTAQUER
    // =============================================================
    @PostMapping("/{combatId}/attack")
    public Combat attaquer(@PathVariable Long combatId) {
        return combatService.actionAttaque(combatId);
    }

    // =============================================================
    // 3️⃣ DÉFENDRE
    // =============================================================
    @PostMapping("/{combatId}/defend")
    public Combat defendre(@PathVariable Long combatId) {
        return combatService.actionDefendre(combatId);
    }

    // =============================================================
    // 4️⃣ UTILISER UN OBJET
    // =============================================================
    @PostMapping("/{combatId}/objet/{objetId}")
    public Combat utiliserObjet(@PathVariable Long combatId, @PathVariable String objetId) {
        Objet objet = objetRepository.findById(objetId)
            .orElseThrow(() -> new RuntimeException("Objet non trouvé : " + objetId));
        return combatService.actionObjet(combatId, objet);
    }

    // =============================================================
    // 5️⃣ RÉCUPÉRER L'ÉTAT D’UN COMBAT
    // =============================================================
    @GetMapping("/{combatId}")
    public Combat getCombat(@PathVariable Long combatId) {
        return combatService.loadCombat(combatId); // Ajouter public si private
    }
    
    
}
