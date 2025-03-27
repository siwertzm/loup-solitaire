package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.model.Utilisateur;
import com.loupsolitaire.backend.repository.UtilisateurRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:8100") // pour éviter le CORS ici aussi
public class UserController {

    private final UtilisateurRepository utilisateurRepository;

    public UserController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping
    public List<Utilisateur> getAllUsers() {
        return utilisateurRepository.findAll();
    }
}
