package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Chapitre;
import com.loupsolitaire.backend.repository.ChapitreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chapitres")
@RequiredArgsConstructor
public class ChapitreController {

  private final ChapitreRepository chapitreRepository;

  // Récupérer tous les chapitres
  @GetMapping
  public List<Chapitre> getAll() {
    return chapitreRepository.findAll();
  }

  // Récupérer un chapitre par son id
  @GetMapping("/{id}")
  public Optional<Chapitre> getById(@PathVariable Integer id) {
    return chapitreRepository.findById(id);
  }

}
