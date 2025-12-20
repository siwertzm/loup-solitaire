package com.loupsolitaire.backend.repository;

import com.loupsolitaire.backend.model.Chapitre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapitreRepository extends JpaRepository<Chapitre, Integer> {

   @Query("SELECT oc.valeur FROM ObjetChap oc WHERE oc.chapitre.id = :chapitreId AND oc.objet.id = :objetId")
        Integer getQuantiteChapitre(@Param("chapitreId") int chapitreId, 
                                    @Param("objetId") String objetId);

}
