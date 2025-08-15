package com.microserviceNote.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateNoteRequest {
    @NotNull
    @Min(1)
    Integer patientId;
    @NotBlank
    String content;
}
