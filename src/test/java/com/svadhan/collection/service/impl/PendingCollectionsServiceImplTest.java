package com.svadhan.collection.service.impl;

import static org.junit.jupiter.api.Assertions.*;


import com.svadhan.collection.repository.CustomerRepository;
import com.svadhan.collection.util.DateTimeUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;



    @ActiveProfiles("Test")
    @SpringBootTest

    @ComponentScan(basePackages = "com.svadhan.collection",
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = {}
            ))
    class CollectionsServiceImplTest {
        @Autowired
        CustomerRepository customerRepository;

        @Test


    }
