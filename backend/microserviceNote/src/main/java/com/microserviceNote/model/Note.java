package com.microserviceNote.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;
import java.time.Instant;

@Document(collection = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    private String id;
    @Indexed
    private Integer patientId; // ou UUID/Long, choisis un format stable inter-MS
    private String content;
    private Instant createdAt = Instant.now();
}
