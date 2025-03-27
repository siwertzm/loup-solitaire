package com.loupsolitaire.backend.config;

import com.loupsolitaire.backend.model.Joueur;
import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.JoueurRepository;
import com.loupsolitaire.backend.repository.ObjetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import com.loupsolitaire.backend.model.Discipline;
import com.loupsolitaire.backend.repository.DisciplineRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JoueurInitLoader implements CommandLineRunner {

  private final JoueurRepository joueurRepository;
  private final ObjetRepository objetRepository;
  private final DisciplineRepository disciplineRepository;

  @Override
  public void run(String... args) throws Exception {

    if (!joueurRepository.findAll().isEmpty()) return;

    Objet arme = objetRepository.findById("hache")
        .orElseThrow(() -> new RuntimeException("Objet non trouvé"));

    Objet repas = objetRepository.findById("repas")
        .orElseThrow(() -> new RuntimeException("Objet non trouvé"));

    Discipline discipline = disciplineRepository.findById("maitrise_des_armes")
        .orElseThrow(() -> new RuntimeException("Discipline non trouvée"));

    Objet armeMaitrise = objetRepository.findById("epee")
        .orElseThrow(() -> new RuntimeException("Objet non trouvé"));


    Joueur joueur = new Joueur();
        joueur.setNom("Loup Solitaire");
        joueur.setHabilite(15);
        joueur.setEndurance(20);
        joueur.setChapActuel(0);
        joueur.setArmes(List.of(arme));
        joueur.setRepas(List.of(repas));
        joueur.setDisciplines(List.of(discipline));
        joueur.setArmeMaitrise(List.of(armeMaitrise));

        joueurRepository.save(joueur);
        System.out.println("✅ Joueur initial créé avec succès");
  }
}
