package com.bynature.adapters.out.persistence.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@EnableAutoConfiguration
public abstract class AbstractJpaTest {


    @Autowired
    protected EntityManager testEntityManager;


    protected void clearAndFlush() {
        testEntityManager.clear();
        testEntityManager.flush();
    }

}
