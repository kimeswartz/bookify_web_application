package com.bookify.bookify_app;

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

