package com.microserviceNote.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "notes")
public class Note {
    @Id
    private String id;
    @Indexed
    private Integer patientId; // ou UUID/Long, choisis un format stable inter-MS
    private String content;
    private Instant createdAt = Instant.now();
}
