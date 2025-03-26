package com.loupsolitaire.backend.model;

import java.util.List;
import lombok.Data;

@Data
public class Chapitre {

    private Object chap; // numéro du chapitre
    private String text; // texte du chapitre
    private Boolean combat; // si le chapitre est un combat
    private List<Objet> objet; // liste des objets du chapitre
    private List<Effet> effet; // liste des effets du chapitre
    private List<Ennemi> ennemi; // liste des ennemis du chapitre
    private List<Lien> lien; // liste des liens du chapitre


}
