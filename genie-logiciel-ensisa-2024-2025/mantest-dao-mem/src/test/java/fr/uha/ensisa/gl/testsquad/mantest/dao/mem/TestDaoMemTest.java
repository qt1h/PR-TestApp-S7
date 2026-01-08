package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;

public class TestDaoMemTest {

    private TestDaoMem sut;

    @BeforeEach
    void createTestDao() {
        sut = new TestDaoMem();
        sut.persist(new ManualTest(0, "Manual Test", "Manual Test Description",
                List.of(new Step("step1"), new Step("step2"))));
    }

    @Test
    void testPersist() {
        ManualTest manualTest = new ManualTest(0, "Manual Test", "Manual Test Description");
        sut.persist(manualTest);
        ManualTest output = sut.find(2);
        assertNotNull(output);
        assertEquals(2, output.getId());
        assertEquals(manualTest, output);
        assertEquals(3, sut.getAvailableId());
    }

    @Test
    void testUpdate() {
        ManualTest manualTest = new ManualTest(2, "Manual Test", "Manual Test Description");
        sut.persist(manualTest);
        ManualTest output = sut.find(2);
        assertNotNull(output);
        assertEquals(2, output.getId());
        assertEquals(manualTest, output);
        assertEquals(3, sut.getAvailableId());
        ManualTest updatedManualTest = new ManualTest(2, "Manual Test", "Updated Manual Test Description");
        sut.update(updatedManualTest);
        ManualTest updatedOutput = sut.find(2);
        assertNotNull(updatedOutput);
        assertEquals(2, updatedOutput.getId());
        assertEquals(updatedManualTest, updatedOutput);
        assertEquals(3, sut.getAvailableId());
        assertEquals(2, sut.count());
    }

    @Test
    void testRemove() {
        ManualTest manualTest = new ManualTest(0, "Manual Test", "Manual Test Description");
        sut.persist(manualTest);
        sut.remove(manualTest.getId());
        ManualTest output = sut.find(manualTest.getId());
        assertNull(output);
    }

    @Test
    void testCount() {
        long count = 1;
        assertEquals(count, sut.count());
        ManualTest manualTest = new ManualTest(0, "Manual Test", "Manual Test Description");
        sut.persist(manualTest);
        count = 2;
        assertEquals(count, sut.count());
        sut.remove(manualTest.getId());
        count = 1;
        assertEquals(count, sut.count());
    }

    @Test
    void testFind() {
        sut.remove(1);
        ManualTest manualTest = new ManualTest(0, "Manual Test", "Manual Test Description");
        sut.persist(manualTest);
        assertEquals(manualTest, sut.find(manualTest.getId()));
    }

    @Test
    void testFindAll() {
        sut.remove(1);
        ManualTest manualTest2 = new ManualTest(0, "Manual Test", "Manual Test Description");
        ManualTest manualTest3 = new ManualTest(0, "Manual Test", "Manual Test Description");
        sut.persist(manualTest2);
        sut.persist(manualTest3);
        Collection<ManualTest> manualTests = sut.findAll();
        assertTrue(manualTests.contains(manualTest2));
        assertTrue(manualTests.contains(manualTest3));
    }

    @Test
    void testClear() {
        ManualTest manualTest2 = new ManualTest(0, "Manual Test", "Manual Test Description");
        sut.persist(manualTest2);

        sut.reset();
        ManualTest output1 = sut.find(1);
        ManualTest output2 = sut.find(2);
        assertNull(output1);
        assertNull(output2);

        assertEquals(0, sut.count());
        assertEquals(1, sut.getAvailableId());

    }

}
