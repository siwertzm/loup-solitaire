package com.loupsolitaire.backend.repository;

import com.loupsolitaire.backend.model.Ennemi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnnemiRepository extends JpaRepository<Ennemi, String> {

}
