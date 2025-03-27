package com.loupsolitaire.backend.repository;

import com.loupsolitaire.backend.model.Effet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EffetRepository extends JpaRepository<Effet, Long> {

}
