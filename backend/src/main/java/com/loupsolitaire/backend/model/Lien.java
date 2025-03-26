package com.loupsolitaire.backend.model;

import lombok.Data;
import java.util.List;

@Data
public class Lien {

  private String page; // numéro de la page
  private List<Cond> cond; // conditions du lien
}
