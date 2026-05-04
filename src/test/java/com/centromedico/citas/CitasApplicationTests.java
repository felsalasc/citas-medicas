package com.centromedico.citas;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CitasApplicationTests.class)
class CitasApplicationTests {

    @BeforeEach
    void setUp() {
        System.out.println("Antes de cada test");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Después de cada test");
    }

    @Test
    void contextLoads() {
        Assertions.assertTrue(true);
    }
}