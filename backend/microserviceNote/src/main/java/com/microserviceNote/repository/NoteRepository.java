package com.microserviceNote.repository;


import com.microserviceNote.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {
    Page<Note> findByPatientIdOrderByCreatedAtDesc(Integer patientId, Pageable pageable);
}
