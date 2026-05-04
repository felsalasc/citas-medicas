package com.centromedico.citas;

import com.centromedico.citas.service.CitaServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        CitasApplicationTests.class,
        CitaServiceTest.class
})
public class MainTestSuite {
}