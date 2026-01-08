package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;

public class SuiteTestDaoMemTest {

    private SuiteTestDao dao;

    @BeforeEach
    public void setUp() {
        dao = new SuiteTestDaoMem();
        dao.reset(); // Assure un état propre pour chaque test
    }

    @Test
    @DisplayName("persist() assigns an id and stores the suite")
    public void testPersist() {
        ManualSuiteTest suite = new ManualSuiteTest(0, "Suite A", "Description A");
        dao.persist(suite);

        // L'id doit être assigné à 1 puisque availableId démarre à 1.
        assertEquals(1, suite.getId(), "The suite id should be set to 1 after persist");

        ManualSuiteTest found = dao.find(1);
        assertNotNull(found, "The suite should be found after persist");
        assertEquals(suite, found, "The persisted suite should equal the found suite");
        // Création d'une deuxième suite et persistance
        ManualSuiteTest suite2 = new ManualSuiteTest(0, "Suite B", "Description B");
        dao.persist(suite2);
        assertEquals(2, suite2.getId(), "La deuxième suite doit avoir l'id 2");
    }

    @Test
    @DisplayName("update() updates the stored suite")
    public void testUpdate() {
        ManualSuiteTest suite = new ManualSuiteTest(0, "Suite A", "Description A");
        dao.persist(suite);

        // Modification de la suite
        suite.setName("Suite A Updated");
        dao.update(suite);

        ManualSuiteTest updated = dao.find(suite.getId());
        assertNotNull(updated, "Updated suite should not be null");
        assertEquals("Suite A Updated", updated.getName(), "The suite name should be updated");
    }

    @Test
    @DisplayName("remove() removes the suite by id")
    public void testRemove() {
        ManualSuiteTest suite = new ManualSuiteTest(0, "Suite A", "Description A");
        dao.persist(suite);
        long id = suite.getId();

        dao.remove(id);
        assertNull(dao.find(id), "The suite should be null after removal");
    }

    @Test
    @DisplayName("find() returns the correct suite")
    public void testFind() {
        ManualSuiteTest suite = new ManualSuiteTest(0, "Suite A", "Description A");
        dao.persist(suite);
        long id = suite.getId();

        ManualSuiteTest found = dao.find(id);
        assertNotNull(found, "find() should return a non-null suite");
        assertEquals(suite, found, "find() should return the correct suite");
    }

    @Test
    @DisplayName("findAll() returns all stored suites")
    public void testFindAll() {
        ManualSuiteTest suite1 = new ManualSuiteTest(0, "Suite A", "Description A");
        ManualSuiteTest suite2 = new ManualSuiteTest(0, "Suite B", "Description B");
        dao.persist(suite1);
        dao.persist(suite2);

        Collection<ManualSuiteTest> all = dao.findAll();
        assertEquals(2, all.size(), "findAll() should return 2 suites");
        assertTrue(all.contains(suite1), "The collection should contain suite1");
        assertTrue(all.contains(suite2), "The collection should contain suite2");
    }

    @Test
    @DisplayName("count() returns the correct number of stored suites")
    public void testCount() {
        assertEquals(0, dao.count(), "Count should be 0 initially");

        ManualSuiteTest suite1 = new ManualSuiteTest(0, "Suite A", "Description A");
        dao.persist(suite1);
        assertEquals(1, dao.count(), "Count should be 1 after one persist");

        ManualSuiteTest suite2 = new ManualSuiteTest(0, "Suite B", "Description B");
        dao.persist(suite2);
        assertEquals(2, dao.count(), "Count should be 2 after two persists");
    }

    @Test
    @DisplayName("reset() clears the store and resets availableId")
    public void testReset() {
        ManualSuiteTest suite1 = new ManualSuiteTest(0, "Suite A", "Description A");
        dao.persist(suite1);
        assertEquals(1, dao.count(), "Store should contain 1 suite");

        dao.reset();
        assertEquals(0, dao.count(), "After reset, store should be empty");

        // Après reset, l'id disponible doit redevenir 1
        ManualSuiteTest suite2 = new ManualSuiteTest(0, "Suite B", "Description B");
        dao.persist(suite2);
        assertEquals(1, suite2.getId(), "After reset, the first persisted suite should get id 1");
    }
}
