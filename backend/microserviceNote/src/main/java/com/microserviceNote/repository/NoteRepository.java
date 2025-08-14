package com.microserviceNote.repository;


import com.microserviceNote.model.Note;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByPatientIdOrderByCreatedAtDesc(String patientId, Pageable pageable);
}
