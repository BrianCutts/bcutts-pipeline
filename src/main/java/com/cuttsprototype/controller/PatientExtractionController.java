package com.cuttsprototype.controller;

import com.cuttsprototype.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * POST Controller to receive raw fhir json from an external source for FHIR patients.
 */

@RestController
@RequestMapping("api/v1/patient")
public class PatientExtractionController {
    private final PatientService patientService;

    public PatientExtractionController(PatientService patientService) {
        this.patientService = patientService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    void receivePatientFHIR(@RequestBody String rawFhirJson) {
        patientService.insertPatientData(rawFhirJson);
    }

}
