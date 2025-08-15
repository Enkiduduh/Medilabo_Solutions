package com.microservicePrevoyance.client;

import com.microservicePrevoyance.model.NoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "notesClient", url = "${clients.notes.base-url}")
public interface NotesClient {
    @GetMapping(value = "/api/notes/{patientId}", headers = "Accept=application/json")
    List<NoteDto> listNotes(@PathVariable("patientId") Integer patientId,
                            @RequestParam int page,
                            @RequestParam int size);

//    @GetMapping(value = "/api/notes/search", headers = "Accept=application/json")
//    List<NoteDto> searchNotes(@RequestParam Integer patientId,
//                              @RequestParam String q,
//                              @RequestParam(defaultValue = "any") String mode,
//                              @RequestParam int page,
//                              @RequestParam int size);

}
