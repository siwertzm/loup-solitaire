package com.loupsolitaire.backend.model;

import lombok.Data;

@Data
public class Cond {

  private TypeCondition type; // type de la condition
  private String id; // id de la condition
  private Object valeur; // valeur de la condition

}
