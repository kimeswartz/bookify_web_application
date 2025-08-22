package com.bookify.bookify_app.config;

import com.bookify.bookify_app.model.Clinic;
import com.bookify.bookify_app.repository.ClinicRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedClinics(ClinicRepository repo) {
        return args -> {
            if (repo.findBySubdomain("hudvardskliniken").isEmpty()) {
                Clinic clinic = new Clinic();
                clinic.setName("Hudvårdskliniken");
                clinic.setSubdomain("hudvardskliniken");
                clinic.setPhone("+46701234567");
                clinic.setEmail("kontakt@hudvardskliniken.se");
                repo.save(clinic);
                System.out.println("✅ Seeded clinic: hudvardskliniken");
            }
        };
    }
}
