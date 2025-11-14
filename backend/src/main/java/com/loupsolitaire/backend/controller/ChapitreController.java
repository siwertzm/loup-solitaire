package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Chapitre;
import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.repository.ChapitreRepository;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.service.ChapitreService;
import com.loupsolitaire.backend.service.JoueurActifService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/chapitres")
@RequiredArgsConstructor
public class ChapitreController {

  private final ChapitreRepository chapitreRepository;
  private final JoueurActifService joueurActifService;
  private final JoueurRepository joueurRepository;
  private final ChapitreService chapitreService;

  // Récupérer tous les chapitres
  @GetMapping
  public List<Chapitre> getAll() {
    return chapitreRepository.findAll();
  }

  // Récupérer un chapitre par son id
  @GetMapping("/{id}")
  public Optional<Chapitre> getById(@PathVariable Integer id, @AuthenticationPrincipal UserDetails userDetails) {

    // Récupérer le joueur actif ===========================================================
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
        throw new RuntimeException("Aucun joueur actif défini pour cet utilisateur");
    }
    Joueur joueur = joueurRepository.findById(joueurId)
            .orElseThrow(() -> new RuntimeException("Joueur non trouvé"));
    // =====================================================================================
    int chapitreActuel = joueur.getChapActuel();
    
    chapitreService.chapSuivant(joueur, id, chapitreActuel);
    
    if (id != chapitreActuel) {
        chapitreService.objetChapitre(joueur, chapitreRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Chapitre non trouvé : " + id)));
        chapitreService.effetChapitre(joueur, chapitreRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Chapitre non trouvé : " + id)));
    }
    return chapitreRepository.findById(id);
  }

  @GetMapping("/lien")
  public List<Integer> getLiens(@AuthenticationPrincipal UserDetails userDetails) {

    // Récupérer le joueur actif
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
        throw new RuntimeException("Aucun joueur actif défini pour cet utilisateur");
    }
    Joueur joueur = joueurRepository.findById(joueurId)
            .orElseThrow(() -> new RuntimeException("Joueur non trouvé"));

    return chapitreService.getLienChapActuel(joueur);
  }
  

}
