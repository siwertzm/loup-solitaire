package com.loupsolitaire.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum IdDiscipline {
    CAMOUFLAGE,
    CHASSE,
    SIXEME_SENS,
    ORIENTATION,
    GUERISON,
    MAITRISE_ARMES,
    BOUCLIER_PSYCHIQUE,
    PUISSANCE_PSYCHIQUE,
    COMMUNICATION_ANIMALE,
    MAITRISE_MATIERE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static IdDiscipline fromString(String value) {
        if (value == null) return null;
        return switch (value.trim().toLowerCase()) {
            case "camouflage" -> CAMOUFLAGE;
            case "chasse" -> CHASSE;
            case "sixieme_sens", "sixième_sens" -> SIXEME_SENS;
            case "orientation" -> ORIENTATION;
            case "guerison", "guérison" -> GUERISON;
            case "maitrise_des_armes" -> MAITRISE_ARMES;
            case "bouclier_psychique" -> BOUCLIER_PSYCHIQUE;
            case "puissance_psychique" -> PUISSANCE_PSYCHIQUE;
            case "communication_animale" -> COMMUNICATION_ANIMALE;
            case "maitrise_matiere" -> MAITRISE_MATIERE;
            default -> {
                System.err.println("⚠️ Discipline inconnue ignorée : " + value);
                yield null;
            }
        };
    }
}   