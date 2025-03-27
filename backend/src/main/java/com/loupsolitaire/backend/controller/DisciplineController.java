package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Discipline;
import com.loupsolitaire.backend.repository.DisciplineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/disciplines")
@RequiredArgsConstructor
public class DisciplineController {

  private final DisciplineRepository disciplineRepository;

  // Récupérer toutes les disciplines
  @GetMapping
  public List<Discipline> getAll() {
    return disciplineRepository.findAll();
  }

  // Récupérer une discipline par son id
  @GetMapping("/{id}")
  public Optional<Discipline> getById(@PathVariable String id) {
    return disciplineRepository.findById(id);
  }

}
