package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;

public class TestExecutionDaoMemTest {

    private TestExecutionDaoMem sut;

    @BeforeEach
    void createTestDao() {
        sut = new TestExecutionDaoMem();
        sut.persist(new ManualTestExecution(0, new ManualTest()));
    }

    @Test
    void testPersist() {
        ManualTestExecution manualStepTestExecution = new ManualTestExecution(0,
                new ManualTest(2, "testName"));
        sut.persist(manualStepTestExecution);
        ManualTestExecution output = sut.find(2);
        assertNotNull(output);
        assertEquals(2, output.getId());
        assertEquals(manualStepTestExecution, output);
        assertEquals(3, sut.getAvailableId());
    }

    @Test
    void testUpdate() {
        ManualTestExecution manualStepTestExecution = new ManualTestExecution(2,
                new ManualTest(2, "testName"));
        manualStepTestExecution.setComment("comment");
        sut.persist(manualStepTestExecution);
        ManualTestExecution output = sut.find(2);
        assertNotNull(output);
        assertEquals(2, output.getId());
        assertEquals(manualStepTestExecution, output);
        assertEquals(3, sut.getAvailableId());

        ManualTestExecution updatedManualStepTestExecution = new ManualTestExecution(2,
                new ManualTest(2, "testName"));
        updatedManualStepTestExecution.setComment("New comment");
        sut.update(updatedManualStepTestExecution);
        ManualTestExecution updatedOutput = sut.find(2);
        assertNotNull(updatedOutput);
        assertEquals(2, updatedOutput.getId());
        assertEquals(updatedManualStepTestExecution, updatedOutput);
        assertEquals(3, sut.getAvailableId());
        assertEquals(2, sut.count());
    }

    @Test
    void testRemove() {
        ManualTestExecution manualStepTestExecution = new ManualTestExecution(2,
                new ManualTest(2, "testName"));
        manualStepTestExecution.setComment("comment");
        sut.persist(manualStepTestExecution);
        sut.remove(manualStepTestExecution.getId());
        ManualTestExecution output = sut.find(manualStepTestExecution.getId());
        assertNull(output);
    }

    @Test
    void testRemoveByTestId() {
        ManualTest manualStepTest = new ManualTest(2, "testName");
        ManualTestExecution manualStepTestExecution = new ManualTestExecution(2, manualStepTest);
        ManualTestExecution manualStepTestExecution2 = new ManualTestExecution(3, manualStepTest);
        manualStepTestExecution.setComment("comment");
        manualStepTestExecution2.setComment("comment2");
        sut.persist(manualStepTestExecution);
        sut.persist(manualStepTestExecution2);
        assertEquals(3, sut.count());
        sut.removeByTestId(manualStepTest.getId());
        ManualTestExecution output = sut.find(manualStepTestExecution.getId());
        assertNull(output);
        assertEquals(1, sut.count());
    }

    @Test
    void testCount() {
        long count = 1;
        assertEquals(count, sut.count());
        ManualTestExecution manualStepTestExecution = new ManualTestExecution(2,
                new ManualTest(2, "testName"));
        manualStepTestExecution.setComment("comment");
        sut.persist(manualStepTestExecution);
        count = 2;
        assertEquals(count, sut.count());
        sut.remove(manualStepTestExecution.getId());
        count = 1;
        assertEquals(count, sut.count());
    }

    @Test
    void testFind() {
        sut.remove(1);
        ManualTestExecution manualStepTestExecution = new ManualTestExecution(2,
                new ManualTest(2, "testName"));
        manualStepTestExecution.setComment("comment");
        sut.persist(manualStepTestExecution);
        assertEquals(manualStepTestExecution, sut.find(manualStepTestExecution.getId()));
    }

    @Test
    void testFindAll() {
        sut.remove(1);
        ManualTestExecution manualStepTestExecution2 = new ManualTestExecution(2,
                new ManualTest(2, "testName"));
        manualStepTestExecution2.setComment("comment");
        ManualTestExecution manualStepTestExecution3 = new ManualTestExecution(3,
                new ManualTest(2, "testName"));
        manualStepTestExecution3.setComment("comment");
        sut.persist(manualStepTestExecution2);
        sut.persist(manualStepTestExecution3);
        Collection<ManualTestExecution> manualTests = sut.findAll();
        assertTrue(manualTests.contains(manualStepTestExecution2));
        assertTrue(manualTests.contains(manualStepTestExecution3));
    }

    @Test
    void testClear() {
        ManualTestExecution manualStepTestExecution = new ManualTestExecution(2,
                new ManualTest(2, "testName"));
        manualStepTestExecution.setComment("comment");
        sut.persist(manualStepTestExecution);

        sut.reset();
        ManualTestExecution output1 = sut.find(1);
        ManualTestExecution output2 = sut.find(2);
        assertNull(output1);
        assertNull(output2);

        assertEquals(0, sut.count());
        assertEquals(1, sut.getAvailableId());

    }

}
