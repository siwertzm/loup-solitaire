package com.loupsolitaire.backend.service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Lien;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.model.TypeCondition;
import com.loupsolitaire.backend.model.Chapitre;
import com.loupsolitaire.backend.model.IdDiscipline;
import com.loupsolitaire.backend.repository.ChapitreRepository;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.ObjetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChapitreService {

    private final ChapitreRepository chapitreRepository;
    private final JoueurRepository joueurRepository;
    private final ObjetRepository objetRepository;
    private final ObjetService objetServices;


    //=================================================================================
    // Passage au chapitre suivant
    //=================================================================================

    public void chapSuivant(Joueur joueur, int idChapitre, int chapitreActuel) {

        //même chapitre, rien faire
        if (idChapitre == chapitreActuel) return;

        Chapitre chapitreActuelle = chapitreRepository.findById(chapitreActuel)
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé : " + chapitreActuel));

        //lien correspondant au chapitre demandé
        Lien lienChoisi = chapitreActuelle.getLien().stream()
            .filter(lien -> Integer.parseInt(lien.getPage()) == idChapitre)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Lien vers chapitre " + idChapitre + " introuvable"));

        // Vérifier si le lien est accessible
        boolean accessible = validationLien(lienChoisi, joueur);
        if (!accessible) {
            throw new RuntimeException("Chapitre " + idChapitre + " inaccessible depuis le chapitre " + chapitreActuel);
        }

        // Effets des conditions
        if (lienChoisi.getCond() != null) {
            lienChoisi.getCond().forEach(cond -> {
                if (cond.getType() == TypeCondition.BOURSE) {
                    Objet or = objetRepository.findById("or")
                        .orElseThrow(() -> new RuntimeException("Objet or introuvable"));
                    int qte = Math.abs(Integer.parseInt(cond.getValeur()));
                    objetServices.retirerObjet(joueur, or, qte);
                }
            });
        }
        // Passage au chapitre suivant
        jeuHasard(joueur);
        joueur.setChapActuel(idChapitre);
        joueurRepository.save(joueur);
    }

    //=================================================================================
    // Validation des conditions d'un lien
    //=================================================================================

    private boolean validationLien(Lien lien, Joueur joueur) {
        // Si aucune condition => accessible
        if (lien.getCond() == null || lien.getCond().isEmpty()) {
            return true;
        }

        // Vérifier si le lien est accessible
        return lien.getCond().stream().allMatch(cond -> {
            TypeCondition type = cond.getType() != null ? cond.getType() : null;
            if (type == null) {
                System.err.println("⚠️ Condition sans type : " + cond);
                return false;
            }

            String valeur = cond.getValeur();
            String target = cond.getTargetId() != null ? cond.getTargetId().trim().toLowerCase() : "";
                    
            switch (type) {
                case BOURSE:
                    return valideBourse(valeur, joueur, target);
                case DISCIPLINE:
                    return valideDiscipline(joueur, target);
                case ENDURANCE:
                    return valideEndurance(valeur, joueur);
                case FUITE:
                        return true; // La fuite est toujours possible;
                case HASARD:
                    return valideHasard(valeur, joueur);
                case OBJET:
                    return valideObjet(joueur, target);
                default : {
                    System.err.println("⚠️ Type de condition inconnu : " + type);
                return false;
                }
            }
        });
    }

    //=================================================================================
    // Methodes de validation des conditions
    //=================================================================================

    private boolean valideBourse(String valeur, Joueur joueur, String target) {
        // Implémenter la logique de validation de la bourse ici
        if (joueur.getBourses() == null || joueur.getBourses().isEmpty()) return false;

        long quantite = joueur.getBourses().stream()
        .filter(objet -> objet.getId().equalsIgnoreCase(target))
        .count();

        try {
            int nbr = Integer.parseInt(valeur);
            return quantite >= nbr;
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Valeur invalide : " + valeur);
            return false;
        }
    }

    private boolean valideDiscipline(Joueur joueur, String disciplineId) {
        IdDiscipline cible;
        try {
            cible = IdDiscipline.fromString(disciplineId);
            if (cible == null) {
                System.err.println("⚠️ Discipline inconnue : " + disciplineId);
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("⚠️ Discipline invalide : " + disciplineId);
            return false;
        }

        if (joueur.getDisciplines() == null || joueur.getDisciplines().isEmpty()) {
            return false;
        }

        return joueur.getDisciplines().stream()
            .anyMatch(discipline -> discipline.getId() == cible);
        
    }

    private boolean valideEndurance(String valeur, Joueur joueur) {
        try {
            int valeurEndu = Integer.parseInt(valeur);
            return joueur.getEndurance() >= valeurEndu;
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Valeur invalide : " + valeur);
            return false;
        }
    }

    private boolean valideHasard(String valeur, Joueur joueur) {
        // Implémenter la logique de validation du hasard ici
        Integer tirage = joueur.getDernierHasard();

        try {
            String contenu = valeur.substring(1, valeur.length() -1);
            String[] parts = contenu.split(",");

            if (parts.length != 2) {
                System.err.println("⚠️ Valeur de hasard invalide : " + valeur);
                return false;
            }

            int min = Integer.parseInt(parts[0].trim());
            int max = Integer.parseInt(parts[1].trim());

            boolean resultat = tirage >= min && tirage <= max;
            return resultat;
        } catch (Exception e) {
            System.err.println("⚠️ Valeur de hasard invalide : " + valeur);
            return false;
        }
        
    }

    private boolean valideObjet(Joueur joueur, String target) {
        if (joueur.getObjets() == null && joueur.getObjets().isEmpty() 
        && joueur.getObjetSpeciaux() == null && joueur.getObjetSpeciaux().isEmpty() 
        && joueur.getArmes() == null && joueur.getArmes().isEmpty()) {
            return false;
        }

        return joueur.getObjets().stream()
            .anyMatch(objet -> objet.getId().equalsIgnoreCase(target)) ||
            joueur.getObjetSpeciaux().stream()
            .anyMatch(objet -> objet.getId().equalsIgnoreCase(target)) ||
            joueur.getArmes().stream()
            .anyMatch(objet -> objet.getId().equalsIgnoreCase(target));
    }

    // Jeu de hasard
    public void jeuHasard (Joueur joueur) {
        int tirage = (int) (ThreadLocalRandom.current().nextDouble() * 10);
        joueur.setDernierHasard(tirage);
        joueurRepository.save(joueur);
        System.out.println("🎲 Nouveau jet de hasard : " + tirage);
    }

    //check lien
    public List<Integer> getLienChapActuel(Joueur joueur) {
        // Récupérer le chapitre actuel du joueur
        int chapActuel = joueur.getChapActuel();
        
        // Récupérer le chapitre depuis le repository
        Chapitre chapitre = chapitreRepository.findById(chapActuel)
            .orElseThrow(() -> new RuntimeException("Chapitre non trouvé : " + chapActuel));
        
        // 3️⃣ Retourne la liste des IDs de chapitres accessibles
        return chapitre.getLien().stream()
            .map(lien -> {
                if (validationLien(lien, joueur)) {
                    return Integer.parseInt(lien.getPage());
                } else {
                    return null;
                }
            })
            .toList();
    }

    //=================================================================================
    // Méthodes Objets
    //=================================================================================

    public void objetChapitre(Joueur joueur, Chapitre chapitre) {
        if (chapitre.getObjet() == null || chapitre.getObjet().isEmpty()) {
            return;
        }
        chapitre.getObjet().forEach(objetChap -> {
            if (!objetChap.isOptionnel()) {
                int quantite = Math.abs(objetChap.getValeur());
                if (objetChap.getValeur() < 0) {
                    objetServices.retirerObjet(joueur, objetChap.getObjet(), quantite);
                } else {
                    objetServices.ajouterObjet(joueur, objetChap.getObjet(), quantite);
                }
            }
        });
    }
}
