package com.bynature;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class AbstractByNatureTest {

    @Autowired
    protected EntityManager testEntityManager;

    protected void clearAndFlush() {
        testEntityManager.clear();
        testEntityManager.flush();
    }
}
