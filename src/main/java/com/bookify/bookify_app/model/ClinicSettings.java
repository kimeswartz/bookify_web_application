// ClinicSettings.java
package com.bookify.bookify_app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Setter
@Getter
@Document("clinic_settings")
public class ClinicSettings {

    @Id
    private String id;

    private String clinicId;
    private int slotIntervalMinutes;
    private List<String> openingHours;
    private List<String> specialOpenings;
    private List<String> specialClosings;

}
