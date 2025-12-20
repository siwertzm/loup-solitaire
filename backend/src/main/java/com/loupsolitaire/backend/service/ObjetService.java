package com.loupsolitaire.backend.service;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.loupsolitaire.backend.model.CategorieObjet;
import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.model.TypeEffet;
import com.loupsolitaire.backend.repository.JoueurRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ObjetService {

    private final JoueurRepository joueurRepository;


    // ============================================================
    // Vérification possession d'objets
    // ============================================================

    public boolean hasObjet(Joueur joueur, Objet objet) {
    if (joueur == null || objet == null) return false;

        return Stream.of(
        joueur.getObjetSpeciaux(),
        joueur.getArmes(),
        joueur.getObjets(),
        joueur.getRepas(),
        joueur.getBourses()
        )
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .anyMatch(o -> o.getId().equalsIgnoreCase(objet.getId()));
    }

    // ============================================================
    // Ajout et retrait d'objets
    // ============================================================

    @Transactional
    public Joueur ajouterObjet(Joueur joueur, Objet objet, int quantite) {
        if (joueur == null || objet == null) {
            throw new IllegalArgumentException("❌ Joueur ou objet invalide");
        }

        CategorieObjet categorie = objet.getCategorie();
        if (categorie == null) {
            System.out.println("⚠️ Objet sans catégorie : " + objet.getNom());
            return joueur;
        }

        switch (categorie) {
            case OBJETS_SPECIAUX -> ajouterObjetSpeciaux(joueur, objet, quantite);
            case ARME -> ajouterArme(joueur, objet, quantite);
            case OBJET, REPAS -> {
                for (int i = 0; i < quantite; i++) {
                    if (!hasPlace(joueur)) {
                        throw new RuntimeException("⚠️ Le sac à dos est plein. Impossible d'ajouter plus d'objets ou de repas.");
                    }
                    if (categorie == CategorieObjet.OBJET) {
                        joueur.getObjets().add(objet);
                    } else {
                        joueur.getRepas().add(objet);
                    }
                }
            }
            case BOURSE -> ajouterOr(joueur, objet, quantite);
            default -> {
                System.out.println("⚠️ Catégorie inconnue : " + categorie);
                return joueur;
            }
        }
        joueurRepository.save(joueur);

        return joueur;
    }

    @Transactional
    public Joueur retirerObjet(Joueur joueur, Objet objet, int quantite) {
        if (joueur == null || objet == null) {
           throw new IllegalArgumentException("Joueur ou objet invalide");
        }
        if (quantite <= 0) {
            throw new IllegalArgumentException("⚠️ La quantité doit être positive.");
        }

        if (objet.getCategorie() != CategorieObjet.REPAS) {
            if (!hasObjet(joueur, objet)) {
                throw new IllegalArgumentException("⚠️ Le joueur ne possède pas cet objet : " + objet.getNom());
            }
        }

        CategorieObjet categorie = objet.getCategorie();

        switch (categorie) {
            case OBJETS_SPECIAUX -> retirerObjetSpeciaux(joueur, objet, quantite);
            case ARME -> retirerArme(joueur, objet, quantite);
            case OBJET -> {
                for (int i = 0; i < quantite; i++) {
                        joueur.getObjets().remove(objet);
                    }
            }
            case REPAS -> {
                joueur.getRepas().remove(objet);
            }
            case BOURSE -> retirerOr(joueur, objet, quantite);
            default -> {
                System.out.println("⚠️ Catégorie inconnue : " + categorie);
                return joueur;
            }
        }
        return joueurRepository.save(joueur);
    }

    // ============================================================
    // Ajout et retrait d'objets spéciaux
    // ============================================================

    private void ajouterObjetSpeciaux(Joueur joueur, Objet objet, int quantite) {

        if (joueur == null || objet == null) {
            throw new IllegalArgumentException("❌ Joueur ou objet invalide");
        }

        // ⚙️ Vérifie les objets uniques (non cumulables)
        if (isObjetUnique(objet)) {
            boolean dejaPossede = joueur.getObjetSpeciaux().stream()
                .anyMatch(o -> o.getId().equalsIgnoreCase(objet.getId()));
            if (dejaPossede) {
                throw new RuntimeException("⚠️ Le joueur possède déjà l'objet spécial unique : " + objet.getNom());
            }
        }

        for (int i = 0; i < quantite; i++) {
            joueur.getObjetSpeciaux().add(objet);
        }

        if (objet.getEffet() != null && !objet.getEffet().isEmpty()) {
            objet.getEffet().stream()
                .filter(e -> e.getType() == TypeEffet.ENDURANCE)
                .forEach(e -> {
                    joueur.setEndurance(joueur.getEndurance() + e.getValeur());
                    joueur.setEnduranceMax(joueur.getEnduranceMax() + e.getValeur());
                });
        }
    }

    private void retirerObjetSpeciaux(Joueur joueur, Objet objet, int quantite) {
        // Vérifie que le joueur possède bien cet objet
        if (!hasObjet(joueur, objet)) {
            throw new RuntimeException("⚠️ Le joueur ne possède pas cet objet spécial : " + objet.getNom());
        }

        for (int i = 0; i < quantite; i++) {
            joueur.getObjetSpeciaux().remove(objet);
        }

        if (objet.getEffet() != null && !objet.getEffet().isEmpty()) {
            objet.getEffet().stream()
                .filter(e -> e.getType() == TypeEffet.ENDURANCE)
                .forEach(e -> {
                    int nouvelleEndu = joueur.getEndurance() - e.getValeur();
                    joueur.setEndurance(Math.max(0, nouvelleEndu));
                    joueur.setEnduranceMax(joueur.getEnduranceMax() - e.getValeur());
                });
        }
    }

    private boolean isObjetUnique(Objet objet) {
        if (objet == null || objet.getId() == null) return false;

        return switch (objet.getId().toLowerCase()) {
            case "cotte_de_mailles", "casque" -> true;
            default -> false;
        };
    }

    // ============================================================
    // Ajout et retrait d'armes
    // ============================================================

    private void ajouterArme(Joueur joueur, Objet arme, int quantite) {

        // Vérifie le nombre maximum d’armes
        int tailleActuelle = joueur.getArmes().size();
        if (tailleActuelle + quantite > 2) {
            throw new RuntimeException("⚠️ Le joueur a déjà le maximum d’armes (2).");
        }

        for (int i = 0; i < quantite; i++) {
            joueur.getArmes().add(arme);
        }
        bonusMaitrise(joueur);
    }

    private void retirerArme(Joueur joueur, Objet arme, int quantite) {

        // Vérifie que le joueur possède bien cet objet
        if (!hasObjet(joueur, arme)) {
            throw new RuntimeException("⚠️ Le joueur ne possède pas cette arme : " + arme.getNom());
        }

        for (int i = 0; i < quantite; i++) {
            joueur.getArmes().remove(arme);
        }
        bonusMaitrise(joueur);
    }

    public void bonusMaitrise(Joueur joueur) {
        int base = joueur.getHabiliteBase();
        int bonus = 0;

        if (joueur.getArmes() == null || joueur.getArmes().isEmpty()) {
            bonus = -4;
        } 
        else {
            // +2 par arme maîtrisée équipée
            long armesMaitrisees = joueur.getArmes().stream()
                .filter(arme -> joueur.getArmeMaitrise().stream()
                    .anyMatch(maitrise -> maitrise.getId().equalsIgnoreCase(arme.getId())))
                .count();
            bonus = (int) armesMaitrisees * 2;
        }

        joueur.setHabilite(base + bonus);
        joueur.setBonusMaitrise(bonus != 0);
    }

    // ============================================================
    // Ajout de piece d'or
    // ============================================================

    private void ajouterOr(Joueur joueur, Objet piece, int quantite) {

        int total = joueur.getBourses().size();
        int capacity = 50 - total;

        if (capacity <= 0) {
            throw new RuntimeException("💰 Votre bourse est déjà pleine (50 pièces maximum).");
        }

        int toAdd = Math.min(quantite, capacity);

        for (int i = 0; i < toAdd; i++) {
            joueur.getBourses().add(piece);
        }
    }

    public void retirerOr(Joueur joueur, Objet piece, int quantite) {
        if (!hasObjet(joueur, piece)) {
            throw new IllegalArgumentException("⚠️ Le joueur ne possède pas de " + piece.getNom());
        }
        // Retire une pièce d’or
        for (int i = 0; i < quantite; i++) {
            joueur.getBourses().remove(piece);
        }
    }

    // ============================================================
    // place limite sac a dos
    // ============================================================

    private boolean hasPlace(Joueur joueur) {
        if (joueur == null) {
            throw new IllegalArgumentException("❌ Joueur invalide");
        }

        int nbObjets = (joueur.getObjets() != null) ? joueur.getObjets().size() : 0;
        int nbRepas = (joueur.getRepas() != null) ? joueur.getRepas().size() : 0;

        int total = nbObjets + nbRepas;

        return total < 8;
    }
}
