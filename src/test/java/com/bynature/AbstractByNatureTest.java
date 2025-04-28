package com.bynature;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public abstract class AbstractByNatureTest {

    @Autowired
    private EntityManager testEntityManager;

    protected void clearAndFlush() {
        testEntityManager.clear();
        testEntityManager.flush();
    }
}
