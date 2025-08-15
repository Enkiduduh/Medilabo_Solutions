package com.microservicePrevoyance.model;

import java.time.Instant;

public record NoteDto(String id, Integer patientId, String content, Instant createdAt) {
}
