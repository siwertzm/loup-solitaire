package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "utilisateur")
@Data
public class Utilisateur {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  private String password;


  @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
  private List<Joueur> joueurs;

}
