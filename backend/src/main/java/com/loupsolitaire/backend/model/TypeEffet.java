package com.loupsolitaire.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TypeEffet {

    ENDURANCE("endurance"),
    HABILETE("habilite"),
    VOL("vol");

    private final String value;

    TypeEffet(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TypeEffet fromValue(String value) {
        for (TypeEffet type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown effet type: " + value);
    }
}
