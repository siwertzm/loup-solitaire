package com.loupsolitaire.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class ObjetPris {

    @Id
    @GeneratedValue
    private Long id;

    private int chapitreId;
    private String objetId;
    private int quantite;

    @ManyToOne
    private Joueur joueur;
    
}
