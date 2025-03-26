package com.loupsolitaire.backend.repository;

import com.loupsolitaire.backend.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, String> {


}
