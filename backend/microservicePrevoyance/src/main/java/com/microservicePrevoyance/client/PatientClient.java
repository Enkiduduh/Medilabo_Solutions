package com.microservicePrevoyance.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "patientClient", url = "${clients.patient.base-url}")
public interface PatientClient {
    @GetMapping(value = "/api/patients/{id}", headers = "Accept=application/json")
    Map<String, Object> getPatient(@PathVariable("id") Integer id);
}