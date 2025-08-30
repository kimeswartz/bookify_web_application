package com.bookify.bookify_app;

// ********************************************************************************************
// * RoomRepositoryTest: verifies MongoDB unique index on room names per clinic               *
// *                                                                                          *
// * WHAT                                                                                     *
// * - Sets up MongoDB indexes for Room entity before each test.                              *
// * - Saves one room with (clinicId, name).                                                  *
// * - Attempts to save another room with the same (clinicId, name).                          *
// * - Expects DuplicateKeyException from MongoDB unique index.                               *
// *                                                                                          *
// * WHY                                                                                      *
// * - Prevents duplicate room names inside the same clinic.                                  *
// * - Protects data integrity at the database level (not just via service validation).       *
// * - Ensures that index annotations/config on Room entity are effective.                    *
// ********************************************************************************************

import com.bookify.bookify_app.model.Room;
import com.bookify.bookify_app.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class RoomRepositoryTest {

    @Autowired
    RoomRepository repo;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        MongoMappingContext mappingContext = (MongoMappingContext) mongoTemplate
                .getConverter()
                .getMappingContext();
        MongoPersistentEntityIndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        resolver.resolveIndexFor(Room.class).forEach(index ->
                mongoTemplate.indexOps(Room.class).ensureIndex(index)
        );
    }

    @Test
    void shouldEnforceUniqueRoomNamePerClinic() {
        Room r1 = new Room();
        r1.setClinicId("clinicB");
        r1.setName("Room 1");
        repo.save(r1);

        Room r2 = new Room();
        r2.setClinicId("clinicB");
        r2.setName("Room 1");

        assertThrows(DuplicateKeyException.class, () -> repo.save(r2));
    }
}

