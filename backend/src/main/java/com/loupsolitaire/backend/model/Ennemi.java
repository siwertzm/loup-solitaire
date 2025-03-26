package com.loupsolitaire.backend.model;

import java.util.List;
import lombok.Data;

@Data
public class Ennemi {

  private String nom; // nom de l'ennemi
  private String description; // description de l'ennemi
  private Integer habilite; // points d'attaque de l'ennemi
  private Integer endurance; // points de vie de l'ennemi
  private List<Discipline> resistance; // résistances de l'ennemi

}
