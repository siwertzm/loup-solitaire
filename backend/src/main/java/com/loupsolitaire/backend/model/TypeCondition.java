package com.loupsolitaire.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeCondition {
    DISCIPLINE("discipline"),
    OBJET("objet"),
    BOURSE("bourse"),
    HASARD("hasard"),
    FUITE("fuite"),
    ARME("arme"),
    ENDURANCE("endurance"),
    ENDURANCE_PERDUE("endurance_perdue"),
    ASSAUT_MAX("assaut_max"),
    ASSAUT_ECHEC("assaut_echec");

    private final String value;

    TypeCondition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TypeCondition fromValue(String value) {
        for (TypeCondition tc : values()) {
            if (tc.value.equalsIgnoreCase(value)) {
                return tc;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }
}
