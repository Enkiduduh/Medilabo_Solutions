package com.microservicePrevoyance.service;

import com.microservicePrevoyance.client.PatientClient;
import com.microservicePrevoyance.client.NotesClient;
import com.microservicePrevoyance.model.PatientNotes;
import com.microservicePrevoyance.model.NoteDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PrevoyanceService {
    private final PatientClient patientClient;
    private final NotesClient notesClient;

    public PrevoyanceService(PatientClient patientClient, NotesClient notesClient) {
        this.patientClient = patientClient;
        this.notesClient = notesClient;
    }

    public PatientNotes getPatientWithNotes(Long patientId, int page, int size) {
        Map<String,Object> patient = patientClient.getPatient(patientId);
        List<NoteDto> notes = notesClient.listNotes(patientId, page, size);
        return new PatientNotes(patient, notes);
    }
}
