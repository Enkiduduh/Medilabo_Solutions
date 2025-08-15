package com.microservicePrevoyance.client;

import com.microservicePrevoyance.model.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "notesClient", url = "${clients.notes.base-url}")
public interface NotesClient {
    @GetMapping("/api/notes/{patientId}")
    List<NoteDto> listNotes(@PathVariable("patientId") Long patientId,
                            @RequestParam int page,
                            @RequestParam int size);

}
