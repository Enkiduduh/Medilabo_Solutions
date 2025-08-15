package com.microservicePrevoyance.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "patientClient", url = "${clients.patient.base-url}")
public interface PatientClient {
    @GetMapping("/api/patients/{id}")
    Map<String, Object> getPatient(@PathVariable("id") Long id);
}