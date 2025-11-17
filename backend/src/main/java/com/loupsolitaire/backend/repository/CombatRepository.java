package com.loupsolitaire.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.loupsolitaire.backend.model.Combat;

@Repository
public interface CombatRepository extends JpaRepository<Combat, Long> {
    
}
