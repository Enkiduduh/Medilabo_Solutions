package com.microserviceNote.service;

import com.microserviceNote.model.CreateNoteRequest;
import com.microserviceNote.model.Note;
import com.microserviceNote.repository.NoteRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public List<Note> getAllNotes(@PathVariable Integer patientId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return noteRepository
                .findByPatientIdOrderByCreatedAtDesc(patientId, PageRequest.of(page, size))
                .getContent();

    }

    public Note create(CreateNoteRequest req) {
        Note n = new Note();
        n.setPatientId(req.getPatientId());
        n.setContent(req.getContent()
                .replace("\r\n", "\n")
                .replace("\r", "\n"));
        if (n.getCreatedAt() == null) n.setCreatedAt(Instant.now());
        return noteRepository.save(n);
    }

}
