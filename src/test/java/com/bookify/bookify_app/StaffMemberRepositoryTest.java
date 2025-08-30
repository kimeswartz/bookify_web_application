package com.bookify.bookify_app;

// ********************************************************************************************
// * StaffMemberRepositoryTest: verifies MongoDB unique index constraints                     *
// *                                                                                          *
// * WHAT                                                                                     *
// * - Runs with @DataMongoTest: in-memory MongoDB test slice (repo + template).              *
// * - BeforeEach: resolves and ensures MongoDB indexes for StaffMember entity.               *
// * - Test: saving two StaffMembers with the same clinicId + email should fail.              *
// *                                                                                          *
// * WHY                                                                                      *
// * - Ensures database-level uniqueness is actually enforced (not just assumed in code).     *
// * - Protects against data corruption (duplicate staff emails within the same clinic).      *
// * - Catches misconfigurations where indexes aren't created at runtime.                     *
// * - Repository tests validate persistence rules, not only service logic.                   *
// ********************************************************************************************

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

