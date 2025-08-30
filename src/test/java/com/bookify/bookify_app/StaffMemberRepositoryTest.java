package com.bookify.bookify_app;

import com.bookify.bookify_app.model.StaffMember;
import com.bookify.bookify_app.repository.StaffMemberRepository;
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
class StaffMemberRepositoryTest {

    @Autowired
    StaffMemberRepository repo;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void setup() {
        MongoMappingContext mappingContext = (MongoMappingContext) mongoTemplate
                .getConverter()
                .getMappingContext();
        MongoPersistentEntityIndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);

        resolver.resolveIndexFor(StaffMember.class).forEach(index ->
                mongoTemplate.indexOps(StaffMember.class).ensureIndex(index) // OBS: deprecated i 4.5+
        );
    }

    @Test
    void shouldEnforceUniqueEmailPerClinic() {
        StaffMember s1 = new StaffMember();
        s1.setClinicId("clinicA");
        s1.setEmail("test@clinic.com");
        s1.setName("Anna");
        repo.save(s1);

        StaffMember s2 = new StaffMember();
        s2.setClinicId("clinicA");
        s2.setEmail("test@clinic.com");
        s2.setName("Erik");

        assertThrows(DuplicateKeyException.class, () -> repo.save(s2));
    }
}

