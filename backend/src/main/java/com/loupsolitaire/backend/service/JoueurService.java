package com.loupsolitaire.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loupsolitaire.backend.model.Discipline;
import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.ObjetRepository;
import com.loupsolitaire.backend.repository.JoueurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JoueurService {

    private final ObjetRepository objetRepository;
    private final JoueurRepository joueurRepository;

    @Autowired
    private ObjetService objetService;


    private final Random random = new Random();

    // ============================================================
    // INITIALISATION DU JOUEUR
    // ============================================================

    public void initialiserJoueur(Joueur joueur) {
        joueur.setHabilite(initHabilite());
        joueur.setHabiliteBase(joueur.getHabilite());
        joueur.setHabiliteTemporaire(0);
        joueur.setEnduranceTemporaire(0);
        joueur.setEndurance(initEndurance());
        joueur.setEnduranceMax(joueur.getEndurance());
        joueur.setChapActuel(0);

        //or aléatoire
        int orInitial = initOr();
        System.out.println("💰 Or initial : " + orInitial);
        Objet objetOr = objetRepository.findById("or")
            .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
        objetService.ajouterObjet(joueur, objetOr, orInitial);

        //ajout objet spécial
        Objet carte = objetRepository.findById("carte")
            .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
        objetService.ajouterObjet(joueur, carte, 1);

        //ajout repas
        Objet repas = objetRepository.findById("repas")
            .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
        objetService.ajouterObjet(joueur, repas, 1);

        //ajout arme
        Objet hache = objetRepository.findById("hache")
            .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
        objetService.ajouterObjet(joueur, hache, 1);

        //ajout objet aléatoire
        Objet objetAleatoire = getObjetAleatoire();
        ajouterObjetAleatoire(joueur, objetAleatoire);
    }

    // ============================================================
    // INIT STATS DE BASE
    // ============================================================

    public int initEndurance() {
        return random.nextInt(10) + 20;
    }

    public int initHabilite() {
        return random.nextInt(10) + 10;
    }

    public int initOr() {
        return random.nextInt(10);
    }

    // ============================================================
    // OBJETS
    // ============================================================

    public Objet getObjetAleatoire() {
        List<String> objetsIds = List.of(
            "epee", "casque", "repas", "cotte_de_mailles", "masse",
            "baton", "lance", "or", "glaive", "potion_de_soin"
        );

        String randomId = objetsIds.get(random.nextInt(objetsIds.size()));
        return objetRepository.findById(randomId)
            .orElseThrow(() -> new RuntimeException("Objet non trouvé"));
    }

    private void ajouterObjetAleatoire(Joueur joueur, Objet objetAleatoire) {
        System.out.println("🎁 Objet aléatoire attribué : " + objetAleatoire.getNom() + " (Catégorie : " + objetAleatoire.getCategorie() + ")");
        if ("repas".equalsIgnoreCase(objetAleatoire.getCategorie())) {
            // Exemple : un joueur reçoit deux repas identiques au début
            objetService.ajouterObjet(joueur, objetAleatoire, 2);
        } else if ("bourse".equalsIgnoreCase(objetAleatoire.getCategorie())) {
           objetService.ajouterObjet(joueur, objetAleatoire,12);
        } else {
            objetService.ajouterObjet(joueur, objetAleatoire, 1);
        }
    }

    // ============================================================
    // DISCIPLINES
    // ============================================================

    private boolean peutAjouterDiscipline(Joueur joueur) {
        return joueur.getDisciplines().size() < 5;
    }

    public Joueur ajouterDiscipline(Joueur joueur, Discipline discipline) {
        if (!peutAjouterDiscipline(joueur)) {
            throw new RuntimeException("❌ Le joueur ne peut pas avoir plus de 5 disciplines.");
        }

        boolean dejaPossede = joueur.getDisciplines().stream()
                .anyMatch(d -> d.getId().equals(discipline.getId()));
        if (dejaPossede) {
            throw new RuntimeException("Le joueur possède déjà cette discipline !");
        }

        // Cas particulier : maîtrise d'armes
        if (discipline.getNom().equalsIgnoreCase("Maîtrise des Armes")) {
            List<Objet> armesDisponibles = objetRepository.findAll().stream()
                    .filter(obj -> obj.getCategorie().equalsIgnoreCase("ARME"))
                    .toList();

            if (!armesDisponibles.isEmpty()) {
                Objet armeAleatoire = armesDisponibles.get(new Random().nextInt(armesDisponibles.size()));

                if (joueur.getArmeMaitrise() == null) {
                    joueur.setArmeMaitrise(new ArrayList<>());
                }

                boolean dejaMaitrise = joueur.getArmeMaitrise().stream()
                        .anyMatch(a -> a.getId().equals(armeAleatoire.getId()));

                if (!dejaMaitrise) {
                    joueur.getArmeMaitrise().add(armeAleatoire);
                }
            }
        }

        joueur.getDisciplines().add(discipline);
        objetService.bonusMaitrise(joueur);
        return joueur;
    }

    // ============================================================
    // ENDURANCE
    // ============================================================

    public Joueur modifierEndurance(Joueur joueur, int montant) {
        int limiteMax = joueur.getEnduranceMax();
        int nouvelleEndurance = joueur.getEndurance() + montant;
        if (nouvelleEndurance > limiteMax) {
            nouvelleEndurance = limiteMax;
        } else if (nouvelleEndurance < 0) {
            nouvelleEndurance = 0;
        }
        joueur.setEndurance(nouvelleEndurance);
        return joueurRepository.save(joueur);
        
    }

    // ============================================================
    // consomation objet
    // ============================================================

    public Joueur consomerObjet(Joueur joueur, Objet objet) {
        if (joueur == null || objet == null) {
            throw new IllegalArgumentException("❌ Joueur ou objet invalide");
        }

        if (!objetService.hasObjet(joueur, objet)) {
            throw new IllegalArgumentException("⚠️ Le joueur ne possède pas cet objet : " + objet.getNom());
        }

        if (!"objet".equalsIgnoreCase(objet.getCategorie())) {
            throw new IllegalArgumentException("⚠️ L'objet '" + objet.getNom() + "' n'est pas consommable.");
        }

        // 🔍 Vérifie l'effet de l'objet
        if (objet.getEffet() == null || objet.getEffet().isEmpty()) {
            throw new RuntimeException("⚠️ L'objet " + objet.getNom() + " n'a pas d'effet consommable.");
        }

        // Application des effets
        objet.getEffet().forEach(effet -> {
        switch (effet.getType()) {
            case ENDURANCE -> {
                modifierEndurance(joueur, effet.getValeur());
            }
            case HABILETE -> {
                joueur.setHabiliteTemporaire(joueur.getHabiliteTemporaire() + effet.getValeur());
            }
            default -> System.out.println("⚠️ Type d'effet non géré : " + effet.getType());
        }});

        objetService.retirerObjet(joueur, objet);
        
        return joueurRepository.save(joueur);
    }

}
