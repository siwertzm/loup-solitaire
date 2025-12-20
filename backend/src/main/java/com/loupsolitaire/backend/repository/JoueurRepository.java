package com.loupsolitaire.backend.repository;

import com.loupsolitaire.backend.model.Joueur;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JoueurRepository extends JpaRepository<Joueur, Long> {

    List<Joueur> findByUtilisateurId(Long utilisateurId);
  
}
