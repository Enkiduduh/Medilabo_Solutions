package com.microserviceNote.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "notes")
public class Note {
    @Indexed
    private Integer patientId; // ou UUID/Long, choisis un format stable inter-MS
    private String content;
    private Instant createdAt = Instant.now();
}
