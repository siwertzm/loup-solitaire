package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "ennemi")
@Data
public class Ennemi {

  @Id
  private String id; // identifiant de l'ennemi

  private String nom; // nom de l'ennemi
  private String description; // description de l'ennemi
  private int habilite; // points d'attaque de l'ennemi
  private int endurance; // points de vie de l'ennemi

  @ManyToMany
  @JoinTable(
    name = "ennemi_resistance",
    joinColumns = @JoinColumn(name = "ennemi_id"),
    inverseJoinColumns = @JoinColumn(name = "discipline_id")
  )
  private List<Discipline> resistance; // résistances de l'ennemi

  @ManyToMany(mappedBy = "ennemi")
  @JsonIgnore
  private List<Chapitre> chapitres; // chapitres associés à l'ennemi

}
