package com.microserviceNote.controller;

import com.microserviceNote.model.CreateNoteRequest;
import com.microserviceNote.model.Note;
import com.microserviceNote.repository.NoteRepository;
import com.microserviceNote.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteRepository noteRepository;
    private final NoteService noteService;

    public NoteController(NoteRepository noteRepository, NoteService noteService) {
        this.noteRepository = noteRepository;
        this.noteService = noteService;
    }

    @GetMapping("/{patientId}")
    public List<Note> getAllNotes(@PathVariable Integer patientId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
        return noteService.getAllNotes(patientId, page, size);
    }

    @PostMapping
    public ResponseEntity<Note> create(@Valid @RequestBody CreateNoteRequest req) {
        Note created = noteService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
