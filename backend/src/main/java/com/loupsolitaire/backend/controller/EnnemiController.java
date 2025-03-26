package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Ennemi;
import com.loupsolitaire.backend.repository.EnnemiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ennemis")
@RequiredArgsConstructor
public class EnnemiController {

  public final EnnemiRepository ennemiRepository;

  // Récupérer tous les ennemis
  @GetMapping
  public List<Ennemi> getAll() {
    return ennemiRepository.findAll();
  }

  // Récupérer un ennemi par son id
  @GetMapping("/{id}")
  public Optional<Ennemi> getById(@PathVariable String id) {
    return ennemiRepository.findById(id);
  }

}
