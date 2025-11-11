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
            case OBJETS_SPECIAUX -> {
                return ajouterObjetSpeciaux(joueur, objet);
            }
            case ARME -> {
                return ajouterArme(joueur, objet);
            }
            case OBJET, REPAS -> {
                if (!hasPlace(joueur)) {
                    throw new RuntimeException("🎒 Votre sac à dos est plein (8 objets ou repas maximum)");
                }

                if (categorie == CategorieObjet.OBJET) {
                    joueur.getObjets().add(objet);
                } else {
                    joueur.getRepas().add(objet);
                }
            }
            case BOURSE -> {
                return ajouterOr(joueur, objet, quantite);
            }
            default -> {
                System.out.println("⚠️ Catégorie inconnue : " + categorie);
                return joueur;
            }
        }

        return joueurRepository.save(joueur);
    }

    @Transactional
    public Joueur retirerObjet(Joueur joueur, Objet objet, int quantite) {
        if (joueur == null || objet == null) {
           throw new IllegalArgumentException("Joueur ou objet invalide");
        }

        if (!hasObjet(joueur, objet)) {
            throw new IllegalArgumentException("⚠️ Le joueur ne possède pas cet objet : " + objet.getNom());
        }

        CategorieObjet categorie = objet.getCategorie();
        if (categorie == null) {
            System.out.println("⚠️ Objet sans catégorie : " + objet.getNom());
            return joueur;
        }

        switch (categorie) {
            case OBJETS_SPECIAUX -> {
                return retirerObjetSpeciaux(joueur, objet);
            }   

            case ARME -> {
                return retirerArme(joueur, objet);
            }

            case OBJET, REPAS -> {
                if (categorie == CategorieObjet.OBJET) {
                    joueur.getObjets().remove(objet);
                } else {
                    joueur.getRepas().remove(objet);
                }
            }
            case BOURSE -> {
                return retirerOr(joueur, objet, quantite);
            }
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

    private Joueur ajouterObjetSpeciaux(Joueur joueur, Objet objet) {

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

        joueur.getObjetSpeciaux().add(objet);

        if (objet.getEffet() != null && !objet.getEffet().isEmpty()) {
            objet.getEffet().stream()
                .filter(e -> e.getType() == TypeEffet.ENDURANCE)
                .forEach(e -> {
                    joueur.setEndurance(joueur.getEndurance() + e.getValeur());
                    joueur.setEnduranceMax(joueur.getEnduranceMax() + e.getValeur());
                });
        } 
        joueurRepository.save(joueur);
        return joueur;
    }

    private Joueur retirerObjetSpeciaux(Joueur joueur, Objet objet) {
        // Vérifie que le joueur possède bien cet objet
        if (!hasObjet(joueur, objet)) {
            throw new RuntimeException("⚠️ Le joueur ne possède pas cet objet spécial : " + objet.getNom());
        }

        joueur.getObjetSpeciaux().remove(objet);

        if (objet.getEffet() != null && !objet.getEffet().isEmpty()) {
            objet.getEffet().stream()
                .filter(e -> e.getType() == TypeEffet.ENDURANCE)
                .forEach(e -> {
                    int nouvelleEndu = joueur.getEndurance() - e.getValeur();
                    joueur.setEndurance(Math.max(0, nouvelleEndu));
                    joueur.setEnduranceMax(joueur.getEnduranceMax() - e.getValeur());
                });
        } 
        joueurRepository.save(joueur);
        return joueur;
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

    private Joueur ajouterArme(Joueur joueur, Objet arme) {

        // Vérifie le nombre maximum d’armes
        if (joueur.getArmes().size() >= 2) {
            throw new RuntimeException("⚠️ Le joueur a déjà le maximum d’armes (2).");
        }

        joueur.getArmes().add(arme);
        bonusMaitrise(joueur);

        return joueurRepository.save(joueur);
    }

    private Joueur retirerArme(Joueur joueur, Objet arme) {

        // Vérifie que le joueur possède bien cet objet
        if (!hasObjet(joueur, arme)) {
            throw new RuntimeException("⚠️ Le joueur ne possède pas cette arme : " + arme.getNom());
        }

        joueur.getArmes().remove(arme);
        bonusMaitrise(joueur);

        return joueurRepository.save(joueur);
    }

    public void bonusMaitrise(Joueur joueur) {
        int base = joueur.getHabiliteBase();
        int bonus = 0;

        if (joueur.getArmes() != null && joueur.getArmeMaitrise() != null) {
        // +2 pour chaque arme équipée que le joueur maîtrise
        bonus = (int) joueur.getArmes().stream()
            .filter(arme -> joueur.getArmeMaitrise().stream()
                .anyMatch(maitrise -> maitrise.getId().equalsIgnoreCase(arme.getId())))
            .count() * 2;
        }

        if (joueur.getArmes() == null || joueur.getArmes().isEmpty()) {
            bonus = -4;
        }

        joueur.setHabilite(base + bonus);
        joueur.setBonusMaitrise(bonus != 0);
    }

    // ============================================================
    // Ajout de piece d'or
    // ============================================================

    public Joueur ajouterOr(Joueur joueur, Objet piece, int quantite) {
        if (joueur == null || piece == null) {
            throw new IllegalArgumentException("❌ Joueur ou objet invalide");
        }

        if (quantite <= 0) {
            throw new IllegalArgumentException("⚠️ La quantité d’or à ajouter doit être positive.");
        }

        int totalActuel = joueur.getBourses() != null ? joueur.getBourses().size() : 0;
        int capaciteRestante = 50 - totalActuel;

        if (capaciteRestante <= 0) {
            throw new RuntimeException("💰 Votre bourse est déjà pleine (50 pièces max).");
        }

        // Limite le nombre ajouté pour ne pas dépasser 50
        int aAjouter = Math.min(quantite, capaciteRestante);

        for (int i = 0; i < aAjouter; i++) {
            joueur.getBourses().add(piece);
        }

        return joueurRepository.save(joueur);
    }

    public Joueur retirerOr(Joueur joueur, Objet piece, int quantite) {
        if (joueur == null || piece == null) {
            throw new IllegalArgumentException("❌ Joueur ou objet invalide");
        }

        if (quantite <= 0) {
            throw new IllegalArgumentException("⚠️ La quantité d’or à retirer doit être positive.");
        }

        int totalActuel = joueur.getBourses() != null ? joueur.getBourses().size() : 0;

        if (totalActuel == 0) {
            throw new RuntimeException("⚠️ Le joueur ne possède pas de pièces d’or à retirer.");
        }

        // Retire une pièce d’or
        for (int i = 0; i < quantite && i < totalActuel; i++) {
            joueur.getBourses().remove(piece);
        }

        return joueurRepository.save(joueur);
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
