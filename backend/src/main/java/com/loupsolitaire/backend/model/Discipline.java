package com.loupsolitaire.backend.model;

import java.util.List;
import lombok.Data;

@Data
public class Discipline {

  private String nom; // nom de la discipline
  private String description; // description de la discipline
  private List<Objet> armes; // armes associées à la discipline

}
