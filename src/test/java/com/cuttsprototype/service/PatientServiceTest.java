package com.cuttsprototype.service;

import com.cuttsprototype.model.PatientRow;
import com.cuttsprototype.repository.PatientRowRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PatientServiceTest {

    @Mock
    PatientRowRepository patientRowRepository;

    @InjectMocks
    PatientService patientService;

    @Test
    public void testShouldPopulatePatientRow() {
        // Given
        String validPatientJson = """
            {
              "resourceType": "Patient",
              "id": "test-patient-123",
              "name": [{
                "given": ["Alice"],
                "family": "Smith"
              }],
              "gender": "female",
              "birthDate": "1985-08-15"
            }
        """;

        // When
        patientService.insertPatientData(validPatientJson);

        // Then
        ArgumentCaptor<PatientRow> captor = ArgumentCaptor.forClass(PatientRow.class);
        verify(patientRowRepository, times(1)).save(captor.capture());

        PatientRow saved = captor.getValue();
        assertThat(saved.getPatientId()).isEqualTo("test-patient-123");
        assertThat(saved.getName()).isEqualTo("Alice Smith");
        assertThat(saved.getGender()).isEqualTo("female");
        assertThat(saved.getBirthDate()).isEqualTo(LocalDate.of(1985, 8, 15));
    }

    @Test
    public void testShouldThrowExceptionWithInvalidResource() {
        // Given
        String nonPatientJson = """
            {
              "resourceType": "Observation",
              "id": "obs-001"
            }
        """;

        // Expect
        assertThatThrownBy( () ->  patientService.insertPatientData(nonPatientJson))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Record provided was not Patient data.");

        verifyNoInteractions(patientRowRepository);
    }
}
