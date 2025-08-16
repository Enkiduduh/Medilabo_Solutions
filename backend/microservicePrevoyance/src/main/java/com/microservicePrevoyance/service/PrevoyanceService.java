// src/main/java/com/medilabo/aggregator/service/PrevoyanceService.java
package com.microservicePrevoyance.service;


import com.microservicePrevoyance.client.NotesClient;
import com.microservicePrevoyance.client.PatientClient;
import com.microservicePrevoyance.model.PrevoyanceProps;
import com.microservicePrevoyance.model.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@RequiredArgsConstructor
public class PrevoyanceService {

    public enum RiskCode {NONE, BORDERLINE, IN_DANGER, EARLY_ONSET}

    private final PatientClient patientClient;
    private final NotesClient notesClient;
    private final KeywordRiskEngine engine;
    private final PrevoyanceProps props;

    public RiskResponse computeRisk(Integer patientId) {
        // 1) Récupère patient (via gateway)
        Map<String, Object> patient = patientClient.getPatient(patientId);
        int age = computeAge(extractBirthDate(patient));
        char genre = normalizeGenre(extractGenre(patient)); // 'M' ou 'F'

        // 2) Récupère des notes par pages (récentes)
        int page = 0, size = 100;
        List<NoteDto> buffer = new ArrayList<>();
        while (buffer.size() < props.getMaxNotes()) {
            List<NoteDto> chunk = notesClient.listNotes(patientId, page, size);
            if (chunk == null || chunk.isEmpty()) break;
            buffer.addAll(chunk);

            // early exit si on a déjà assez de mots pour du EARLY_ONSET
            var interim = engine.evaluate(buffer, 8); // 8 = seuil max possible
            if (isEarlyOnset(age, genre, interim.matchedCount())) {
                return toResponse(patientId, age, genre, interim.matchedCount(), interim.matchedKeywords());
            }
            if (chunk.size() < size) break;
            page++;
        }

        var res = engine.evaluate(buffer, 8);
        return toResponse(patientId, age, genre, res.matchedCount(), res.matchedKeywords());
    }

    /* ------------ Règles métiers ------------ */

    private RiskResponse toResponse(Integer pid, int age, char sex, int count, List<String> matched) {
        RiskCode code = classify(age, sex, count);
        String labelFr = switch (code) {
            case NONE -> "Aucun risque";
            case BORDERLINE -> "Risque limité";
            case IN_DANGER -> "Danger";
            case EARLY_ONSET -> "Apparition précoce";
        };
        return new RiskResponse(pid, age, String.valueOf(sex), count, code.name(), labelFr, matched);
    }

    private RiskCode classify(int age, char sex, int c) {
        // None si aucun déclencheur
        if (c == 0) return RiskCode.NONE;

        if (age > 30) {
            if (c >= 8) return RiskCode.EARLY_ONSET;
            if (c >= 6) return RiskCode.IN_DANGER;   // 6 ou 7
            if (c >= 2) return RiskCode.BORDERLINE;  // 2..5
            return RiskCode.NONE; // c == 1
        } else { // âge <= 30
            if (sex == 'M') {
                if (c >= 5) return RiskCode.EARLY_ONSET;
                if (c >= 3) return RiskCode.IN_DANGER;
                return RiskCode.NONE; // 0..2
            } else { // 'F'
                if (c >= 7) return RiskCode.EARLY_ONSET;
                if (c >= 4) return RiskCode.IN_DANGER;
                return RiskCode.NONE; // 0..3
            }
        }
    }

    private boolean isEarlyOnset(int age, char sex, int c) {
        return classify(age, sex, c) == RiskCode.EARLY_ONSET;
    }

    /* ------------ Extraction Patient ------------ */

    private LocalDate extractBirthDate(Map<String, Object> patient) {
        // essaie plusieurs clés possibles
        Object val = firstNonNull(
                patient.get("birthDate"), patient.get("dateNaissance"),
                patient.get("dob"), patient.get("dateOfBirth")
        );
        if (val == null) throw new IllegalStateException("Patient sans date de naissance");
        if (val instanceof String s) {
            // supporte "yyyy-MM-dd" et ISO-8601
            try {
                return LocalDate.parse(s);
            } catch (Exception ignored) {
                return LocalDate.parse(s, DateTimeFormatter.ISO_DATE);
            }
        }
        if (val instanceof LocalDate ld) return ld;
        throw new IllegalStateException("Format de date invalide: " + val);
    }

    private String extractSex(Map<String, Object> patient) {
        Object v = firstNonNull(patient.get("sex"), patient.get("gender"), patient.get("sexe"));
        if (v == null) throw new IllegalStateException("Patient sans sexe");
        return String.valueOf(v);
    }

    private int computeAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now(ZoneId.of("Europe/Paris"))).getYears();
    }

    private String extractGenre(Map<String, Object> patient) {
        Object v = patient.get("genre"); // microservice-patient renvoie "H" ou "F"
        if (v == null) throw new IllegalStateException("Patient sans genre");
        return String.valueOf(v);
    }

    private char normalizeGenre(String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalStateException("Genre vide");
        char c = Character.toUpperCase(raw.trim().charAt(0));
        if (c == 'M' || c == 'F') return c;
        throw new IllegalStateException("Genre invalide (attendu 'M' ou 'F') : " + raw);
    }

    private static Object firstNonNull(Object... vals) {
        for (Object v : vals) if (v != null) return v;
        return null;
    }

    /* ------------ DTO de sortie ------------ */
    public record RiskResponse(
            Integer patientId, int age, String genre,
            int matchedCount,
            String code,         // NONE | BORDERLINE | IN_DANGER | EARLY_ONSET
            String labelFr,      // pour l'affichage
            List<String> matchedKeywords
    ) {
    }
}
