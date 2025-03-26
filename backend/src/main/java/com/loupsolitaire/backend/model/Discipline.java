package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "discipline")
@Data
public class Discipline {

  @Id
  private String id; // identifiant de la discipline

  private String nom; // nom de la discipline

  @Column(length = 3000)
  private String description; // description de la discipline

  @ManyToMany(mappedBy = "resistance")
  @JsonIgnore
  private List<Ennemi> ennemis;

  @ManyToMany
  @JoinTable(
    name = "discipline_arme",
    joinColumns = @JoinColumn(name = "discipline_id"),
    inverseJoinColumns = @JoinColumn(name = "objet_id")
  )
  private List<Objet> armes; // armes associées à la discipline

}
