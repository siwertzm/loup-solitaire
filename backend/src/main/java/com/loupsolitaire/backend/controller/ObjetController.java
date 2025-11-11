package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.ObjetRepository;
import com.loupsolitaire.backend.service.JoueurActifService;
import com.loupsolitaire.backend.service.JoueurService;
import com.loupsolitaire.backend.service.ObjetService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/objets")
@RequiredArgsConstructor
public class ObjetController {

  private final ObjetRepository objetRepository;
  private final JoueurRepository joueurRepository;
  private final ObjetService objetService;
  private final JoueurActifService joueurActifService;
  private final JoueurService joueurService;

  // Récupérer tous les objets
  @GetMapping
  public List<Objet> getAll() {
    return objetRepository.findAll();
  }

  // Récupérer un objet par son id
  @GetMapping("/{id}")
  public Optional<Objet> getById(@PathVariable String id) {
    return objetRepository.findById(id);
  }

  //ajouter un objet a l'utilisateur actif
  @PostMapping("/ajouter/{id}")
  public ResponseEntity<String> ajouterObjet(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
    // Récupérer l'objet par son id
    Objet objet = objetRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Objet non trouvé"));

    // Récupérer le joueur actif
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
      throw new RuntimeException("Aucun joueur actif trouvé pour l'utilisateur");
    }

    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));
    
    // Ajouter l'objet au joueur
    objetService.ajouterObjet(joueur, objet, 1);

    String message = String.format("✅ %s ajouté à %s.", objet.getNom(), joueur.getNom());

    return ResponseEntity.ok(message);
  }

  //retirer un objet a l'utilisateur actif
  @PostMapping("/retirer/{id}")
  public ResponseEntity<String> retirerObjet(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
    // Récupérer l'objet par son id
    Objet objet = objetRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Objet non trouvé"));

    // Récupérer le joueur actif
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
      throw new RuntimeException("Aucun joueur actif trouvé pour l'utilisateur");
    }

    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));

    // retirer l'objet au joueur
    objetService.retirerObjet(joueur, objet);

    String message = String.format("✅ %s retiré de %s.", objet.getNom(), joueur.getNom());

    return ResponseEntity.ok(message);
  }

  //consomer un objet
  @PostMapping("/consomer/{id}")
  public ResponseEntity<String> consomerObjet(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
    // Récupérer l'objet par son id
    Objet objet = objetRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
      
    // Récupérer le joueur actif
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
      throw new RuntimeException("Aucun joueur actif trouvé pour l'utilisateur");
    }

    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));

    // retirer l'objet au joueur
    joueurService.consomerObjet(joueur, objet);

    String message = String.format("✅ %s retiré de %s.", objet.getNom(), joueur.getNom());

    return ResponseEntity.ok(message);
  }
  

}
