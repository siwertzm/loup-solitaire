package com.loupsolitaire.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.loupsolitaire.backend.model.Objet;
import com.loupsolitaire.backend.repository.ObjetRepository;

import com.loupsolitaire.backend.model.Discipline;
import com.loupsolitaire.backend.repository.DisciplineRepository;

import com.loupsolitaire.backend.model.Ennemi;
import com.loupsolitaire.backend.repository.EnnemiRepository;

import com.loupsolitaire.backend.model.Chapitre;
import com.loupsolitaire.backend.repository.ChapitreRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader {

    private final ObjetRepository objetRepository;
    private final DisciplineRepository  disciplineRepository;
    private final EnnemiRepository ennemiRepository;
    private final ChapitreRepository chapitreRepository;

    @PostConstruct
    public void loadData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            InputStream inputStreamObjet = getClass().getClassLoader().getResourceAsStream("data/objet.json");
            InputStream inputStreamDiscipline = getClass().getClassLoader().getResourceAsStream("data/discipline.json");
            InputStream inputStreamEnnemi = getClass().getClassLoader().getResourceAsStream("data/ennemi.json");
            InputStream inputStreamChapitre = getClass().getClassLoader().getResourceAsStream("data/chapitre.json");

            if (inputStreamObjet == null) {
                System.err.println("❌ Fichier 'objet.json' non trouvé dans resources/data");
                return;
            }

            if (inputStreamDiscipline == null) {
              System.err.println("❌ Fichier 'discipline.json' non trouvé dans resources/data");
              return;
            }

            if (inputStreamEnnemi == null) {
              System.err.println("❌ Fichier 'ennemi.json' non trouvé dans resources/data");
              return;
            }

            if (inputStreamChapitre == null) {
              System.err.println("❌ Fichier 'chapitre.json' non trouvé dans resources/data");
              return;
            }

            List<Objet> objets = objectMapper.readValue(inputStreamObjet, new TypeReference<>() {});
            objetRepository.saveAll(objets);

            List<Discipline> disciplines = objectMapper.readValue(inputStreamDiscipline, new TypeReference<>() {});
            disciplineRepository.saveAll(disciplines);

            List<Ennemi> ennemis = objectMapper.readValue(inputStreamEnnemi, new TypeReference<>() {});
            ennemiRepository.saveAll(ennemis);

            List<Chapitre> chapitres = objectMapper.readValue(inputStreamChapitre, new TypeReference<>() {});
            chapitreRepository.saveAll(chapitres);

            System.out.println("✅ Données 'Objet' chargées avec succès : " + objets.size());
            System.out.println("✅ Données 'Discipline' chargées avec succès : " + disciplines.size());
            System.out.println("✅ Données 'Ennemi' chargées avec succès : " + ennemis.size());
            System.out.println("✅ Données 'Chapitre' chargées avec succès : " + chapitres.size());

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des objets : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
