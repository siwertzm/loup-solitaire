package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.ObjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/objets")
@RequiredArgsConstructor
public class ObjetController {

  private final ObjetRepository objetRepository;

  // Récupérer tous les objets
  @GetMapping
  public List<Objet> getAll() {
    return objetRepository.findAll();
  }

  // Récupérer un objet par son id
  @GetMapping("/{id}")
  public Optional<Objet> getById(@PathVariable String id) {
    return objetRepository.findById(id);
  }

}
