package com.loupsolitaire.backend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class JoueurActifService {
    // Associe chaque utilisateur (username) à l'id de son joueur actif
    private final Map<String, Long> joueursActifs = new ConcurrentHashMap<>();

    // Définir le joueur actif pour un utilisateur
    public void setJoueurActif(String username, Long joueurId) {
        joueursActifs.put(username, joueurId);
    }

    // Récupérer le joueur actif
    public Long getJoueurActif(String username) {
        return joueursActifs.get(username);
    }

    // Supprimer le joueur actif (ex: déconnexion)
    public void clearJoueurActif(String username) {
        joueursActifs.remove(username);
    }

    // Vérifier si un joueur actif est défini
    public boolean hasJoueurActif(String username) {
        return joueursActifs.containsKey(username);
    }
}
