package com.loupsolitaire.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.ObjetPris;

public interface ObjetPrisRepository extends JpaRepository<ObjetPris, Long> {
    
    boolean existsByJoueurAndChapitreIdAndObjetId(Joueur joueur, int chapitreId, String objetId);

    Optional<ObjetPris> findByJoueurAndChapitreIdAndObjetId(
        Joueur joueur,
        int chapitreId,
        String objetId
    );

    List<ObjetPris> findAllByJoueurAndChapitreId(
        Joueur joueur, 
        int chapitreId
    );


}