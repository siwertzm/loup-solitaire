package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "joueur")
@Data
public class Joueur {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nom;
  private Integer habilite;
  private Integer habiliteBase;
  private Boolean bonusMaitrise = false;
  private Integer habiliteTemporaire;
  private Integer enduranceTemporaire;
  private Integer endurance;
  private Integer enduranceMax;
  private Integer chapActuel;
  private Long utilisateurId;
  private Integer dernierHasard;
  private boolean kdo = false;

  @ManyToMany
  @JoinTable(
      name = "joueur_discipline_maitrise",
      joinColumns = @JoinColumn(name = "joueur_id"),
      inverseJoinColumns = @JoinColumn(name = "objet_id")
  )
  private List<Objet> armeMaitrise = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "joueur_discipline",
      joinColumns = @JoinColumn(name = "joueur_id"),
      inverseJoinColumns = @JoinColumn(name = "discipline_id")
  )
  private List<Discipline> disciplines = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "joueur_arme",
      joinColumns = @JoinColumn(name = "joueur_id"),
      inverseJoinColumns = @JoinColumn(name = "objet_id")
  )
  private List<Objet> armes = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "joueur_objet",
      joinColumns = @JoinColumn(name = "joueur_id"),
      inverseJoinColumns = @JoinColumn(name = "objet_id")
  )
  private List<Objet> objets = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "joueur_repas",
      joinColumns = @JoinColumn(name = "joueur_id"),
      inverseJoinColumns = @JoinColumn(name = "objet_id")
  )
  private List<Objet> repas = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "joueur_bourse",
      joinColumns = @JoinColumn(name = "joueur_id"),
      inverseJoinColumns = @JoinColumn(name = "objet_id")
  )
  private List<Objet> bourses = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "joueur_objet_special",
      joinColumns = @JoinColumn(name = "joueur_id"),
      inverseJoinColumns = @JoinColumn(name = "objet_id")
  )
  private List<Objet> objetSpeciaux = new ArrayList<>();
}
