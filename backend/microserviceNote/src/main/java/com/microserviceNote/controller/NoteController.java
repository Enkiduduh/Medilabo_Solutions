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
    @Autowired
    NoteRepository noteRepository;

    @GetMapping("/{patientId}")
    public List<Note> list(@PathVariable String patientId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size) {
        return noteRepository.findByPatientIdOrderByCreatedAtDesc(patientId, PageRequest.of(page, size));
    }

}
