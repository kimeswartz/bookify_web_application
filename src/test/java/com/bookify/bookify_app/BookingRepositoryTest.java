package com.bookify.bookify_app;

// ********************************************************************************************
// * BookingRepositoryTest: verifies unique booking rules + ZonedDateTime converters          *
// *                                                                                          *
// * WHAT                                                                                     *
// * - Loads Mongo custom converters (ZonedDateTime <-> Date) via @Import(MongoConverters).   *
// * - Ensures compound unique index on bookings prevents double-booking same slot.           *
// * - Verifies ZonedDateTime fields are stored/retrieved correctly in MongoDB.               *
// *                                                                                          *
// * WHY                                                                                      *
// * - Prevents business rule violations (no overlapping booking for the same staff/room/slot)*
// * - Protects against silent data corruption if converters misbehave.                       *
// * - Database-level tests confirm persistence behavior, not just in-memory logic.           *
// ********************************************************************************************

import com.bookify.bookify_app.config.MongoConvertersConfig;
import com.bookify.bookify_app.model.Booking;
import com.bookify.bookify_app.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(MongoConvertersConfig.class) // 👈 gör att dina converters laddas
class BookingRepositoryTest {

    @Autowired
    BookingRepository repo;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void setupIndexes() {
        MongoMappingContext mappingContext =
                (MongoMappingContext) mongoTemplate.getConverter().getMappingContext();

        MongoPersistentEntityIndexResolver resolver =
                new MongoPersistentEntityIndexResolver(mappingContext);

        resolver.resolveIndexFor(Booking.class).forEach(index ->
                mongoTemplate.indexOps(Booking.class).ensureIndex(index)
        );
    }

    @Test
    void shouldEnforceUniqueBookingCompoundIndex() {
        ZonedDateTime start = ZonedDateTime.now();
        ZonedDateTime end = start.plusMinutes(30);

        Booking b1 = new Booking();
        b1.setClinicId("clinicX");
        b1.setStaffId("staff1");
        b1.setRoomId("room1");
        b1.setTreatmentVariantId("tv1");
        b1.setStartTime(start);
        b1.setEndTime(end);
        b1.setCustomerEmail("a@b.com");
        b1.setCustomerName("Anna");
        b1.setStatus("BOOKED");
        repo.save(b1);

        Booking b2 = new Booking();
        b2.setClinicId("clinicX");
        b2.setStaffId("staff1");
        b2.setRoomId("room1");
        b2.setTreatmentVariantId("tv2");
        b2.setStartTime(start); // samma slot
        b2.setEndTime(end);
        b2.setCustomerEmail("c@d.com");
        b2.setCustomerName("Erik");
        b2.setStatus("BOOKED");

        assertThrows(DuplicateKeyException.class, () -> repo.save(b2));
    }

    @Test
    void shouldStoreAndRetrieveZonedDateTimeCorrectly() {
        ZonedDateTime start = ZonedDateTime.now().withNano(0); // trunc to millis
        ZonedDateTime end = start.plusHours(1);

        Booking booking = new Booking();
        booking.setClinicId("clinicA");
        booking.setStaffId("staff1");
        booking.setRoomId("room1");
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setCustomerEmail("test@example.com");
        booking.setCustomerName("Anna");
        booking.setStatus("BOOKED");

        repo.save(booking);

        Booking found = repo.findById(booking.getId()).orElseThrow();

        // Truncate both sides to millis before assert
        assertEquals(start.toInstant().toEpochMilli(), found.getStartTime().toInstant().toEpochMilli());
        assertEquals(end.toInstant().toEpochMilli(), found.getEndTime().toInstant().toEpochMilli());
    }
}
