package com.cuttsprototype.controller;

import com.cuttsprototype.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientExtractionController.class)
public class PatientExtractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Test
    public void shouldAcceptValidPatientJsonAndReturnCreated() throws Exception {
        // given
        String json = """
            {
              "resourceType": "Patient",
              "id": "abc-123",
              "name": [{
                "given": ["John"],
                "family": "Doe"
              }],
              "gender": "male",
              "birthDate": "1990-01-01"
            }
        """;

        // when + then
        mockMvc.perform(post("/api/v1/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(patientService, times(1)).insertPatientData(json);
    }
}
