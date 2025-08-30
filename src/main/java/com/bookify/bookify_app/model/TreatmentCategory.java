package com.bookify.bookify_app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document("treatment_categories")
public class TreatmentCategory {
    @Id
    private String id;

    private String clinicId;

    @Indexed(unique = true)
    private String name;

    private String description;

}
