package com.loupsolitaire.backend.model;

import java.util.List;
import lombok.Data;

@Data
public class Objet {

  private String nom; // nom de l'objet
  private String description; // description de l'objet
  private String categorie; // effets de l'objet
  private List<Effet> effet; // conditions de l'objet

}
