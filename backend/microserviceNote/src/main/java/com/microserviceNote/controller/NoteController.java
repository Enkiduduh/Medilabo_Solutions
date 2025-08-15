package com.microserviceNote.controller;

import com.microserviceNote.model.Note;
import com.microserviceNote.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteRepository noteRepository;
    @Autowired org.springframework.data.mongodb.core.MongoTemplate template;

    public NoteController(NoteRepository noteRepository) { this.noteRepository = noteRepository; }

    @GetMapping("/{patientId}")
    public List<Note> list(@PathVariable Integer patientId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size) {
        return noteRepository
                .findByPatientIdOrderByCreatedAtDesc(patientId, PageRequest.of(page, size))
                .getContent();
    }


    @GetMapping("/_raw/{patientId}")
    public List<Note> raw(@PathVariable String patientId) {
        var c = new org.springframework.data.mongodb.core.query.Criteria().orOperator(
                org.springframework.data.mongodb.core.query.Criteria.where("patientId").is(Integer.valueOf(patientId)),
                org.springframework.data.mongodb.core.query.Criteria.where("patientId").is(patientId)
        );
        var q = new org.springframework.data.mongodb.core.query.Query(c)
                .with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"))
                .limit(50);
        return template.find(q, Note.class, "notes"); // force "notes"
    }
}
