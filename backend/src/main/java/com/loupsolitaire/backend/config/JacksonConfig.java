package com.loupsolitaire.backend.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();

        // Optionnel : force l'initialisation des lazy
        module.enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);

        return module;
    }
}
