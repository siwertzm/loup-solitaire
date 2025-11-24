package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.model.ObjetPris;
import com.loupsolitaire.backend.repository.ChapitreRepository;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.ObjetPrisRepository;
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



@RestController
@RequestMapping("/objets")
@RequiredArgsConstructor
public class ObjetController {

  private final ObjetRepository objetRepository;
  private final JoueurRepository joueurRepository;
  private final ObjetService objetService;
  private final JoueurActifService joueurActifService;
  private final JoueurService joueurService;
  private final ObjetPrisRepository objetPrisRepository;
  private final ChapitreRepository chapitreRepository;

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
  public ResponseEntity<Joueur> ajouterObjet(@PathVariable String id, @AuthenticationPrincipal UserDetails userDetails) {
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
    
    // recuperer le chapitre
    int chapitreId = joueur.getChapActuel();
    int maxChapitre = chapitreRepository.getQuantiteChapitre(chapitreId, id);
    // Vérifier si l'objet a déjà été pris dans ce chapitre
    ObjetPris prise = objetPrisRepository.findByJoueurAndChapitreIdAndObjetId(joueur, chapitreId, id).orElse(null);

    if (prise != null && prise.getQuantite() >= maxChapitre) {
        return ResponseEntity.status(409).build();
    }

    Joueur updatedJoueur = objetService.ajouterObjet(joueur, objet, 1);

    if (prise == null) {
        prise = new ObjetPris();
        prise.setJoueur(joueur);
        prise.setChapitreId(chapitreId);
        prise.setObjetId(id);
        prise.setQuantite(1);
    } else {
        prise.setQuantite(prise.getQuantite() + 1);
    }
    objetPrisRepository.save(prise);
    
    // Ajouter l'objet au joueur
    

    return ResponseEntity.ok(updatedJoueur);
  }

  @PostMapping("/coffre")
  public ResponseEntity<String> coffreBienvenu(@AuthenticationPrincipal UserDetails userDetails) {
    // Récupérer le joueur actif
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
      throw new RuntimeException("Aucun joueur actif trouvé pour l'utilisateur");
    }

    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));

    if (joueur.isKdo()) {
        return ResponseEntity.badRequest().body("🎉 Coffre de bienvenue déjà attribué au joueur.");
    }
    joueurService.caisseInitiale(joueur);

    return ResponseEntity.ok("Coffre de bienvenue créé pour " + joueur.getNom());
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
    objetService.retirerObjet(joueur, objet, 1);

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

  @GetMapping("/pris/{chapitreId}")
  public ResponseEntity<List<ObjetPris>> getObjetsPris(
        @PathVariable int chapitreId,
        @AuthenticationPrincipal UserDetails userDetails) {

    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur introuvable"));

    List<ObjetPris> prises = objetPrisRepository
        .findAllByJoueurAndChapitreId(joueur, chapitreId);

    return ResponseEntity.ok(prises);
  }
  

}
