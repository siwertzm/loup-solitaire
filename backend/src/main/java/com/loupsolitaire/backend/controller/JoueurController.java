package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.ObjetRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/joueurs")
@RequiredArgsConstructor
public class JoueurController {

  private final JoueurRepository joueurRepository;

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
  public Joueur create(@RequestBody Joueur joueur) {
    joueur.setNom("Marius");
    joueur.setEndurance(0);
    joueur.setHabilite(0);
    joueur.setChapActuel(0);

    //arme par defaut
    Objet arme = objetRepository.findById("hache")
      .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
    joueur.setArmes(List.of(arme));

    //objet par defaut
    Objet repas = objetRepository.findById("repas")
      .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
    joueur.setRepas(List.of(repas));

    return joueurRepository.save(joueur);
  }

}
