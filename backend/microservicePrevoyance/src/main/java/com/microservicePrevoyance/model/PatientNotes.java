package com.microservicePrevoyance.model;

import java.util.List;
import java.util.Map;

public record PatientNotes(Map<String,Object> patient, List<NoteDto> notes) {}