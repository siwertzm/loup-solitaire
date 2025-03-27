package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "effet")
@Data
public class Effet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // identifiant de l'effet

  @Enumerated(EnumType.STRING)
  private TypeEffet type; // type de l'effet
  private Integer valeur; // valeur de l'effet

  @ManyToMany(mappedBy = "effet")
  @JsonIgnore
  private List<Objet> objets; // objets associés à l'effet

  @ManyToMany(mappedBy = "effet")
  @JsonIgnore
  private List<Chapitre> chapitres; // chapitres associés à l'effet

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
    name = "effet_cond",
    joinColumns = @JoinColumn(name = "effet_id"),
    inverseJoinColumns = @JoinColumn(name = "cond_id")
  )
  private List<Cond> cond; // conditions de l'effet


}
