package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "lien")
@Data
public class Lien {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // identifiant du lien

  private String page; // numéro de la page

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
    name = "lien_cond",
    joinColumns = @JoinColumn(name = "lien_id"),
    inverseJoinColumns = @JoinColumn(name = "cond_id")
  )
  
  private List<Cond> cond; // conditions du lien

  @ManyToMany(mappedBy = "lien")
  @JsonIgnore
  private List<Chapitre> chapitres; // chapitres associés au lien
}
