package com.cuttsprototype.service;

import ca.uhn.fhir.context.FhirContext;
import com.cuttsprototype.model.PatientRow;
import com.cuttsprototype.repository.PatientRowRepository;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

/**
 * Service layer for business logic to extract patient information from FHIR standard JSON.
 */

@Service
public class PatientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientService.class);

    private final PatientRowRepository patientRowRepository;

    private final FhirContext fhirContext = FhirContext.forR4();

    public PatientService(PatientRowRepository patientRowRepository) {
        this.patientRowRepository = patientRowRepository;
    }

    public void insertPatientData(String rawJson) {
        IBaseResource resource = fhirContext.newJsonParser().parseResource(rawJson);
        if (resource instanceof Patient patient) {
            PatientRow extractedRow = extractPatient(patient);
            patientRowRepository.save(extractedRow);
        } else {
            throw new IllegalArgumentException("Record provided was not Patient data.");
        }
    }

    private PatientRow extractPatient(Patient patient) {
        PatientRow row = new PatientRow();
        try {
            row.setPatientId(patient.getIdElement().getIdPart());
            String name = patient.getNameFirstRep().getNameAsSingleString();
            String gender = patient.getGender().toCode();
            row.setName(name);
            row.setGender(gender);
            if (patient.getBirthDate() != null) {
                row.setBirthDate(patient.getBirthDate().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate());
            }
            LOGGER.info("Received patient: {}, {} %n", name, gender);

        } catch(Exception e) {
            LOGGER.error("Could not parse patient record{}", e.getMessage());
        }
        return row;
    }
}
