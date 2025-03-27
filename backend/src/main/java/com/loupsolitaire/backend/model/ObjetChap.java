package com.loupsolitaire.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "objet_chap")
@Data
public class ObjetChap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    private Integer valeur;
    private boolean optionnel;

    @ManyToOne
    @JoinColumn(name = "objet_id")
    private Objet objet;

    @ManyToOne
    @JoinColumn(name = "chapitre_id")
    private Chapitre chapitre;

    // ✅ Permet de lire "id": "epee" dans le JSON et l'appliquer à ce champ `objet`
    @JsonProperty("id")
    public void setObjetFromId(String id) {
        Objet obj = new Objet();
        obj.setId(id);
        this.objet = obj;
    }
}
