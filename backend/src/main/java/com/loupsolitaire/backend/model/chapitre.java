package com.loupsolitaire.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "chapitre")
@Data
public class Chapitre {

    @Id
    private Integer id;

    @Column(length = 10000)
    private String text;

    private Boolean combat;

    @OneToMany(mappedBy = "chapitre", cascade = CascadeType.ALL)
    private List<ObjetChap> objet;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "chapitre_effet",
        joinColumns = @JoinColumn(name = "chapitre_id"),
        inverseJoinColumns = @JoinColumn(name = "effet_id")
    )
    private List<Effet> effet;

    @ManyToMany
    @JoinTable(
        name = "chapitre_ennemi",
        joinColumns = @JoinColumn(name = "chapitre_id"),
        inverseJoinColumns = @JoinColumn(name = "ennemi_id")
    )
    private List<Ennemi> ennemi;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
        name = "chapitre_lien",
        joinColumns = @JoinColumn(name = "chapitre_id"),
        inverseJoinColumns = @JoinColumn(name = "lien_id")
    )
    private List<Lien> lien;
}
