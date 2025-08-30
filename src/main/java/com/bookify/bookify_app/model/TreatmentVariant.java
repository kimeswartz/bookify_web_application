// TreatmentVariant.java
package com.bookify.bookify_app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Setter
@Getter
@Document("treatment_variants")
public class TreatmentVariant {
    @Id
    private String id;

    private String clinicId;
    private String treatmentId;
    private String name;
    private BigDecimal price;
    private int durationMinutes;
    private int bufferMinutes;
    private boolean active;

}

