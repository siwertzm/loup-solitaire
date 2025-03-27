package com.loupsolitaire.backend.repository;

import com.loupsolitaire.backend.model.Chapitre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapitreRepository extends JpaRepository<Chapitre, Integer> {

}
