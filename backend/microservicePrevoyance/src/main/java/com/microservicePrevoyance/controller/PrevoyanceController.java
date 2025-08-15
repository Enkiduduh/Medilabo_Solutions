package com.microservicePrevoyance.controller;

import com.microservicePrevoyance.model.PatientNotes;
import com.microservicePrevoyance.service.PrevoyanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prevoyance")
public class PrevoyanceController {

    private final PrevoyanceService service;

    public PrevoyanceController(PrevoyanceService service) {
        this.service = service;
    }

    @GetMapping("/patient/{id}")
    public PatientNotes get(@PathVariable Long id,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return service.getPatientWithNotes(id, page, size);
    }
}