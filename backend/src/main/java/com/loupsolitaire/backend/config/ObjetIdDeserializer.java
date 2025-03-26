package com.loupsolitaire.backend.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.loupsolitaire.backend.model.Objet;

import java.io.IOException;

public class ObjetIdDeserializer extends JsonDeserializer<Objet> {

    @Override
    public Objet deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String id = p.getValueAsString();
        Objet objet = new Objet();
        objet.setId(id);
        return objet;
    }
}
