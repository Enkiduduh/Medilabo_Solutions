package com.microservicePrevoyance.controller;

import com.microservicePrevoyance.service.PrevoyanceService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/prevoyance")
public class PrevoyanceController {

    private final PrevoyanceService service;

    public PrevoyanceController(PrevoyanceService service) {
        this.service = service;
    }

    // GET /api/prevoyance/patient/4/risk
    @GetMapping("/patient/{id}/risk")
    public PrevoyanceService.RiskResponse risk(@PathVariable Integer id) {
        return service.computeRisk(id);
    }
}