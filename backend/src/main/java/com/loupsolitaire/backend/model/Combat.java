package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "combat")
@Data
public class Combat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer tourActuel;
    private Integer reductionDegat;
    private Integer bonusHabilite;
    private boolean combatTermine;
    private Integer hasard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ennemi_id")
    private Ennemi ennemi;


    
}
