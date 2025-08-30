package com.bookify.bookify_app.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import java.time.ZonedDateTime;

@Setter
@Getter
@Document("bookings")
@CompoundIndex(name="unique_booking", def="{ 'clinicId': 1, 'staffId': 1, 'roomId': 1, 'startTime': 1 }", unique=true)
public class Booking {
    @Id
    private String id;

    private String clinicId;
    private String staffId;
    private String roomId;
    private String treatmentVariantId;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private String customerEmail;
    private String customerName;
    private String status; // BOOKED, CANCELLED, COMPLETED
}