package com.loupsolitaire.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CategorieObjet {
    ARME,
    OBJET,
    OBJETS_SPECIAUX,
    REPAS,
    BOURSE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static CategorieObjet fromString(String value) {
        if (value == null) return null;
        return switch (value.trim().toLowerCase()) {
            case "arme" -> ARME;
            case "objet" -> OBJET;
            case "objets spéciaux" -> OBJETS_SPECIAUX;
            case "repas" -> REPAS;
            case "bourse" -> BOURSE;
            default -> {
                System.err.println("⚠️ Catégorie inconnue ignorée : " + value);
                yield null;
            }
        };
    }
}
