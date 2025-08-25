package com.bookify.bookify_app;

// ********************************************************************************************
// * ClinicTenantTest verifies multi-tenant resolution based on request subdomain.            *
// *                                                                                          *
// * Test wiring:                                                                             *
// *  - @SpringBootTest + @AutoConfigureMockMvc spins up the full Spring context and MVC.     *
// *  - A Clinic entity is inserted before each test to simulate a tenant in the database.    *
// *                                                                                          *
// * Covered flows:                                                                           *
// *  - Requests with a known subdomain resolve the correct clinic and return clinicId.       *
// *  - Requests with an unknown subdomain return 404 Not Found.                              *
// *                                                                                          *
// * WHY: Ensures that TenantSubdomainFilter + ClinicService correctly enforce per-tenant     *
// * resolution logic based on subdomains.                                                    *
// ********************************************************************************************

import com.bookify.bookify_app.model.Clinic;
import com.bookify.bookify_app.repository.ClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClinicTenantTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ClinicRepository repo;

    @BeforeEach
    void setup() {
        repo.deleteAll();
        Clinic clinic = new Clinic();
        clinic.setName("Hudvårdskliniken");
        clinic.setSubdomain("hudvardskliniken");
        clinic.setPhone("+46701234567");
        clinic.setEmail("kontakt@hudvardskliniken.se");
        repo.save(clinic);
    }


    @Test
    void shouldResolveClinicFromSubdomain() throws Exception {
        mockMvc.perform(get("/api/v1/admin/clinic")
                        .header("Host", "hudvardskliniken.minapp.se"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.clinicId").exists());
    }

    @Test
    void shouldReturn404ForUnknownClinic() throws Exception {
        mockMvc.perform(get("/api/v1/admin/clinic")
                        .header("Host", "okand.minapp.se"))
                        .andExpect(status().isNotFound());
    }
}
