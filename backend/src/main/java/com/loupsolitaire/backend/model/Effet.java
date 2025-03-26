package com.loupsolitaire.backend.model;

import java.util.List;
import lombok.Data;

@Data
public class Effet {

  private TypeEffet type; // type de l'effet
  private Object valeur; // valeur de l'effet
  private List<Cond> cond; // conditions de l'effet


}
