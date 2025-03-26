package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "objet")
@Data
public class Objet {

  @Id
  private String id; // identifiant de l'objet

  private String nom;
  private String description;
  private String categorie;

  @ManyToMany(mappedBy = "armes")
  @JsonIgnore
  private List<Discipline> disciplines;


  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
    name = "objet_effet",
    joinColumns = @JoinColumn(name = "objet_id"),
    inverseJoinColumns = @JoinColumn(name = "effet_id")
  )
  private List<Effet> effet;

}
