package com.loupsolitaire.backend.service;

import org.springframework.stereotype.Service;

import com.loupsolitaire.backend.model.Cond;
import com.loupsolitaire.backend.model.Effet;
import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.IdDiscipline;
import com.loupsolitaire.backend.repository.JoueurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EffetService {

    private final JoueurService joueurService;

    private final JoueurRepository joueurRepository;

    public void appliquerEffetHabilete(Joueur joueur, Effet effet) {
        Integer valeur = effet.getValeur();

        // Si aucune condition, appliquer directement
        if (effet.getCond() == null || effet.getCond().isEmpty()) {
            if (effet.isPermanent()) {
                joueur.setHabilite(Math.max(0, joueur.getHabilite() + valeur));
                joueurRepository.save(joueur);
            } else {
                joueur.setHabiliteTemporaire(valeur);
                joueurRepository.save(joueur);
            }
            return;
        }

        // Sinon, vérifier les conditions
        Cond cond = effet.getCond().get(0);
        switch (cond.getType()) {
            case DISCIPLINE:
                boolean hasBouclier = joueur.getDisciplines().stream()
                    .anyMatch(d -> d.getId() == IdDiscipline.BOUCLIER_PSYCHIQUE);
                if (!hasBouclier) {
                    joueur.setHabiliteTemporaire(valeur);
                }
                break;
            case OBJET:
                boolean hasTorche = joueur.getObjets().stream()
                    .anyMatch(d -> d.getId().equals("torche"));
                if (!hasTorche) {
                    joueur.setHabiliteTemporaire(valeur);
                }
                break;
            case ASSAUT_MAX:
                joueur.setHabiliteTemporaire(valeur); // attente du système de combat
                break;
            default:
                break;
        }
        joueurRepository.save(joueur);
    }

    public void appliquerEffetEndurance(Joueur joueur, Effet effet) {
        Integer valeur = effet.getValeur();

        // Si aucune condition, appliquer directement
        if (effet.getCond() == null || effet.getCond().isEmpty()) {
            if (valeur != 1000) {
                joueurService.modifierEndurance(joueur, valeur);
            } else {
                // Cas spécial : valeur 1000 signifie restauration complète
                joueur.setEndurance(joueur.getEnduranceMax());
                joueurRepository.save(joueur);
            }
            return;
        }

        Cond cond = effet.getCond().get(0);
        int tirage = joueur.getDernierHasard();
        String limite = cond.getValeur();

        if (verifierHasard(tirage, limite)) {
            if (valeur != 1000) {
                joueurService.modifierEndurance(joueur, valeur);
            } else {
                joueur.setEndurance(joueur.getEnduranceMax());
                joueurRepository.save(joueur);
            }
        }
    }

    public void appliquerEffetVol(Joueur joueur, Effet effet) {
        int valeur = effet.getValeur();

        if (effet.getCond() == null || effet.getCond().isEmpty()) {
            volerSansCondition(joueur, valeur);
            joueurRepository.save(joueur);
            return;
        }

        Cond cond = effet.getCond().get(0);

        switch (cond.getType()) {
            case HASARD -> {
                if (verifierHasard(joueur.getDernierHasard(), cond.getValeur())) {
                    joueur.getObjets().clear();
                    joueur.getRepas().clear();
                    joueurRepository.save(joueur);
                }
            }
            case ARME -> {
                volerArmes(joueur, valeur);
                joueurRepository.save(joueur);
            }
            default -> {
                System.err.println("⚠️ Type de condition de vol inconnu : " + cond.getType());
            }
        }
    }

    private boolean verifierHasard(int tirage, String limite) {
        try {
            String contenu = limite.substring(1, limite.length() - 1);
            String[] parts = contenu.split(",");

            int min = Integer.parseInt(parts[0].trim());
            int max = Integer.parseInt(parts[1].trim());

            return tirage >= min && tirage <= max;

        } catch (Exception e) {
            System.err.println("⚠️ Erreur de parsing sur la valeur de hasard : " + limite);
            return false;
        }
    }

    private void volerSansCondition(Joueur joueur, int montant) {

        // Vol complet des objets (OBJETS + REPAS)
        if (montant == 8) {
            joueur.getObjets().clear();
            joueur.getRepas().clear();
            return;
        }

        // Vol de tout l'équipement (OBJETS + REPAS + ARMES)
        if (montant == 10) {
            joueur.getObjets().clear();
            joueur.getRepas().clear();
            joueur.getArmes().clear();
            return;
        }

        // Vol simple : voler exactement 1 élément
        if (montant == 1) {
            // Priorité 1 : objet
            if (!joueur.getObjets().isEmpty()) {
                joueur.getObjets().remove(0);
                return;
            }
            // Priorité 2 : repas
            if (!joueur.getRepas().isEmpty()) {
                joueur.getRepas().remove(0);
                return;
            }
            // Priorité 3 : arme
            if (!joueur.getArmes().isEmpty()) {
                joueur.getArmes().remove(0);
            }
            return;
        }
    }

    private void volerArmes(Joueur joueur, int montant) {
        if (joueur.getArmes().isEmpty()) {
            return;
        }
        if (montant == 1) {
            joueur.getArmes().remove(0);
        } else {
            joueur.getArmes().clear();
        }
    }




    
}
