package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.ObjetRepository;
import com.loupsolitaire.backend.model.Utilisateur;
import com.loupsolitaire.backend.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;


import ch.qos.logback.classic.pattern.Util;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/joueurs")
@RequiredArgsConstructor
public class JoueurController {

  private final JoueurRepository joueurRepository;
  private final UtilisateurRepository utilisateurRepository;
  private final ObjetRepository objetRepository;

  // Récupérer tous les joueurs
  @GetMapping
  public List<Joueur> getAll() {
    return joueurRepository.findAll();
  }

  // Récupérer un joueur par son id
  @GetMapping("/{id}")
  public Optional<Joueur> getById(@PathVariable Long id) {
    return joueurRepository.findById(id);
  }

  // Ajouter un joueur
  @PostMapping
  public Joueur create(@RequestBody Joueur joueur, Authentication authentication) {

    String username = authentication.getName();
    Utilisateur utilisateur = utilisateurRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    joueur.setUtilisateur(utilisateur);

    int endurance = (int) ((Math.random() * 10) + 20);
    int habilite = (int) ((Math.random() * 10) + 10);

    joueur.setEndurance(endurance);
    joueur.setEnduranceMax(endurance + 3);
    joueur.setHabilite(habilite);
    joueur.setChapActuel(0);

    // 🪓 Arme par défaut
    Objet arme = objetRepository.findById("hache")
        .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
    joueur.setArmes(List.of(arme));

    // 🍗 Objet par défaut
    Objet repas = objetRepository.findById("repas")
        .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
    joueur.setRepas(List.of(repas));

    return joueurRepository.save(joueur);
  }

}
