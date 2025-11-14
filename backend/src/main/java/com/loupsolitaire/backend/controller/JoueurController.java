package com.loupsolitaire.backend.controller;
import com.loupsolitaire.backend.model.Discipline;
import com.loupsolitaire.backend.model.IdDiscipline;
import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Utilisateur;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.UtilisateurRepository;
import com.loupsolitaire.backend.repository.DisciplineRepository;
import com.loupsolitaire.backend.service.JoueurActifService;
import com.loupsolitaire.backend.service.JoueurService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/joueurs")
@RequiredArgsConstructor
public class JoueurController {

  private final JoueurActifService joueurActifService;
  private final JoueurService joueurService;
  private final JoueurRepository joueurRepository;
  private final UtilisateurRepository utilisateurRepository;
  private final DisciplineRepository disciplineRepository;

  //===========================================================
  // commande admin
  //===========================================================

  @PostMapping("/chap/{id}")
  public Joueur initChap(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) {
    // recuper le joueur actif
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
      throw new RuntimeException("Aucun joueur actif défini pour cet utilisateur");
    }
    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));

    joueur.setChapActuel(id);
    joueurRepository.save(joueur);
      
    return joueur;
  }
  
  @PostMapping("/endu/{id}")
  public Joueur initEndu(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer id) {
    // recuper le joueur actif
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
      throw new RuntimeException("Aucun joueur actif défini pour cet utilisateur");
    }
    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));

    joueur.setEndurance(id);
    joueurRepository.save(joueur);
      
    return joueur;
  }
  //===========================================================

  // Récupérer tous les joueurs
  @GetMapping
  public List<Joueur> getAll() {
    return joueurRepository.findAll();
  }

  // Récupérer les joueurs de l'utilisateur connecté
  @GetMapping("/mesjoueurs")
  public List<Joueur> getMesJoueurs(@AuthenticationPrincipal UserDetails userDetails) {
    Utilisateur utilisateur = utilisateurRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    return joueurRepository.findByUtilisateurId(utilisateur.getId());
  }

  // Récupérer un joueur par son id
  @GetMapping("/{id}")
  public Optional<Joueur> getById(@PathVariable Long id) {
    return joueurRepository.findById(id);
  }

  // Ajouter un joueur
  @PostMapping
  public Joueur create(@RequestBody Joueur joueur, @AuthenticationPrincipal UserDetails userDetails) {
    //nom par defaut
    if (joueur.getNom() == null || joueur.getNom().isBlank()) {
    joueur.setNom("Loup Solitaire");
    }

    //recup utilisateur
    Utilisateur utilisateur = utilisateurRepository.findByUsername(userDetails.getUsername())
      .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    joueur.setUtilisateurId(utilisateur.getId());

    //initialisation du joueur
    joueurService.initialiserJoueur(joueur);

    return joueurRepository.save(joueur);
  }

  //selection un jouer actif
  @PutMapping("/actif/{id}")
  public ResponseEntity<String> activerJoueur(
      @PathVariable Long id,
      @AuthenticationPrincipal UserDetails userDetails) {

    Utilisateur utilisateur = utilisateurRepository.findByUsername(userDetails.getUsername())
        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    Joueur joueur = joueurRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Joueur non trouvé ou n'appartient pas à l'utilisateur"));
    
    if (!joueur.getUtilisateurId().equals(utilisateur.getId())) {
        throw new RuntimeException("Ce joueur ne vous appartient pas !");
    }
    joueurActifService.setJoueurActif(userDetails.getUsername(), id);

    return ResponseEntity.ok("Joueur activé !");
  }

  //recup joueur actif
  @GetMapping("/actif")
  public Joueur getJoueurActif(@AuthenticationPrincipal UserDetails userDetails) {
    Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
    if (joueurId == null) {
        throw new RuntimeException("Aucun joueur actif défini pour cet utilisateur");
    }
    return joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));
  }

  //ajouter une discipline
  @PostMapping("/discipline/{disciplineId}")
  public Joueur ajoutDiscipline(
      @PathVariable String disciplineId,
      @AuthenticationPrincipal UserDetails userDetails) {

   // recuper le joueur actif
   Long joueurId = joueurActifService.getJoueurActif(userDetails.getUsername());
   if (joueurId == null) {
       throw new RuntimeException("Aucun joueur actif défini pour cet utilisateur");
    }
    Joueur joueur = joueurRepository.findById(joueurId)
        .orElseThrow(() -> new RuntimeException("Joueur actif non trouvé"));
    
    // recuper la discipline
    IdDiscipline idEnum = IdDiscipline.fromString(disciplineId);
    if (idEnum == null) {
      throw new RuntimeException("⚠️ Discipline inconnue : " + disciplineId);
    }

    Discipline discipline = disciplineRepository.findById(idEnum)
        .orElseThrow(() -> new RuntimeException("Discipline non trouvée : " + idEnum));

    joueurService.ajouterDiscipline(joueur, discipline);
  

    return joueurRepository.save(joueur);
  }
  
}
