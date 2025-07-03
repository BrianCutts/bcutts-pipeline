package com.cuttsprototype.integration;

import com.cuttsprototype.model.PatientRow;
import com.cuttsprototype.repository.PatientRowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class PatientExtractionControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRowRepository repository;

    @Test
    void testPostPatientFHIR_storesToPostgres() throws Exception {
        String fhirJson = """
        {
          "resourceType": "Patient",
          "id": "container-test-id",
          "name": [ { "given": ["Elena"], "family": "Fisher" } ],
          "gender": "female",
          "birthDate": "1983-04-22"
        }
        """;

        mockMvc.perform(post("/api/v1/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fhirJson))
                .andExpect(status().isCreated());

        PatientRow saved = repository.findAll().get(0);
        assertThat(saved.getPatientId()).isEqualTo("container-test-id");
        assertThat(saved.getName()).isEqualTo("Elena Fisher");
        assertThat(saved.getGender()).isEqualTo("female");
        assertThat(saved.getBirthDate()).isEqualTo(LocalDate.of(1983, 4, 22));
    }
}
