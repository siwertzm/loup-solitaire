package com.loupsolitaire.backend.controller;

import com.loupsolitaire.backend.config.JwtUtil;
import com.loupsolitaire.backend.model.Utilisateur;
import com.loupsolitaire.backend.repository.UtilisateurRepository;
import com.loupsolitaire.backend.request.AuthRequest;
import com.loupsolitaire.backend.request.RegisterRequest;
import com.loupsolitaire.backend.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (utilisateurRepository.findByUsername(request.getUsername()).isPresent()) {
            return "Nom d'utilisateur déjà utilisé";
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(request.getUsername());
        utilisateur.setPassword(passwordEncoder.encode(request.getPassword()));
        utilisateurRepository.save(utilisateur);
        return "✅ Utilisateur créé avec succès";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
        return new AuthResponse(token);
    }

    @GetMapping("/me")
    public Utilisateur getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return utilisateurRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
}
