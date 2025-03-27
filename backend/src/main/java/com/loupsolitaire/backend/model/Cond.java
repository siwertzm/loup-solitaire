package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "cond")
@Data
public class Cond {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // identifiant de la condition

  @Enumerated(EnumType.STRING)
  private TypeCondition type; // type de la condition

  private String targetId; // id de la condition

  private String valeur;

  @ManyToMany(mappedBy = "cond")
  @JsonIgnore
  private List<Lien> liens; // liens associés à la condition

  @ManyToMany(mappedBy = "cond")
  @JsonIgnore
  private List<Effet> effets; // effets associés à la condition

}
