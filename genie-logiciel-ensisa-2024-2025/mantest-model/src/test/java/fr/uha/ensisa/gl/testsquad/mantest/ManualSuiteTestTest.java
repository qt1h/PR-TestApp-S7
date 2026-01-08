package fr.uha.ensisa.gl.testsquad.mantest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ManualSuiteTestTest {

    private ManualSuiteTest suite;

    @BeforeEach
    void setUp() {
        suite = new ManualSuiteTest();
    }

    @Test
    @DisplayName("Default constructor: tests list is empty")
    void testDefaultConstructor() {
        List<ManualTest> tests = suite.getTests();
        assertNotNull(tests, "La liste des tests ne doit pas être null");
        assertTrue(tests.isEmpty(), "La liste doit être vide au démarrage");
    }

    @Test
    @DisplayName("Constructor with id, name and description initializes fields correctly")
    void testConstructorWithIdNameDescription() {
        suite = new ManualSuiteTest(1, "Suite A", "Description A");
        assertEquals(1, suite.getId());
        assertEquals("Suite A", suite.getName());
        assertEquals("Description A", suite.getDescription());
        assertTrue(suite.getTests().isEmpty(), "La liste des tests doit être vide");
    }

    @Test
    @DisplayName("Constructor with id and name sets fields correctly")
    void testConstructorWithIdAndName() {
        long expectedId = 5;
        String expectedName = "Test Suite";

        ManualSuiteTest suite = new ManualSuiteTest(expectedId, expectedName);

        assertEquals(expectedId, suite.getId(), "L'id doit être initialisé correctement");
        assertEquals(expectedName, suite.getName(), "Le nom doit être initialisé correctement");
        assertTrue(suite.getTests().isEmpty(), "La liste des tests doit être vide");
        // La description n'est pas initialisée par ce constructeur, donc elle doit être
        // null
        assertNull(suite.getDescription(), "La description doit être null");
    }

    @Test
    @DisplayName("Setters and getters work correctly")
    void testSettersAndGetters() {
        suite.setId(10);
        suite.setName("Test Suite");
        suite.setDescription("Une description");
        assertEquals(10, suite.getId());
        assertEquals("Test Suite", suite.getName());
        assertEquals("Une description", suite.getDescription());
    }

    @Test
    @DisplayName("addTest() adds tests and getTests() returns them")
    void testAddTest() {
        ManualTest test1 = new ManualTest(1, "Test1");
        ManualTest test2 = new ManualTest(2, "Test2");
        suite.addTest(test1);
        suite.addTest(test2);
        List<ManualTest> tests = suite.getTests();
        assertEquals(2, tests.size());
        assertTrue(tests.contains(test1));
        assertTrue(tests.contains(test2));
    }

    @Test
    @DisplayName("getTests() returns a defensive copy")
    void testDefensiveCopy() {
        ManualTest test1 = new ManualTest(1, "Test1");
        suite.addTest(test1);
        List<ManualTest> copy = suite.getTests();
        copy.clear();
        assertEquals(1, suite.getTests().size(),
                "La liste interne ne doit pas être affectée par la modification de la copie");
    }

    @Test
    @DisplayName("removeTest() removes a test by instance")
    void testRemoveTest() {
        ManualTest test1 = new ManualTest(1, "Test1");
        ManualTest test2 = new ManualTest(2, "Test2");
        suite.addTest(test1);
        suite.addTest(test2);
        assertEquals(2, suite.getTests().size());
        suite.removeTest(test1);
        List<ManualTest> tests = suite.getTests();
        assertEquals(1, tests.size());
        assertFalse(tests.contains(test1));
        assertTrue(tests.contains(test2));
    }

    @Test
    @DisplayName("removeTestById() removes a test by its id")
    void testRemoveTestById() {
        ManualTest test1 = new ManualTest(1, "Test1");
        ManualTest test2 = new ManualTest(2, "Test2");
        suite.addTest(test1);
        suite.addTest(test2);
        assertEquals(2, suite.getTests().size());
        suite.removeTestById(1);
        List<ManualTest> tests = suite.getTests();
        assertEquals(1, tests.size());
        assertFalse(tests.contains(test1));
        assertTrue(tests.contains(test2));
    }

    @Test
    @DisplayName("toString returns non-empty string containing suite data")
    void testToString() {
        suite = new ManualSuiteTest(1, "Suite A", "Description A");
        suite.addTest(new ManualTest(1, "Test1", "Desc1"));
        String str = suite.toString();
        assertNotNull(str, "toString ne doit pas retourner null");
        assertFalse(str.isEmpty(), "toString ne doit pas retourner une chaîne vide");
        assertTrue(str.contains("id=1"), "La chaîne doit contenir 'id=1'");
        assertTrue(str.contains("Suite A"), "La chaîne doit contenir le nom de la suite");
        assertTrue(str.contains("Description A"), "La chaîne doit contenir la description de la suite");
        assertTrue(str.contains("tests="), "La chaîne doit contenir 'tests='");
    }

    @Test
    @DisplayName("equals returns true for identical suites")
    void testEqualsIdentical() {
        ManualSuiteTest suite1 = new ManualSuiteTest(1, "Suite A", "Description A");
        ManualSuiteTest suite2 = new ManualSuiteTest(1, "Suite A", "Description A");
        assertTrue(suite1.equals(suite2), "Les suites identiques doivent être égales");
        assertEquals(suite1.hashCode(), suite2.hashCode(),
                "Les hashCode doivent être identiques pour des suites égales");
    }

    @Test
    @DisplayName("equals returns false for different id")
    void testEqualsDifferentId() {
        ManualSuiteTest suite1 = new ManualSuiteTest(1, "Suite A", "Description A");
        ManualSuiteTest suite2 = new ManualSuiteTest(2, "Suite A", "Description A");
        assertFalse(suite1.equals(suite2), "Les suites avec des id différents ne doivent pas être égales");
    }

    @Test
    @DisplayName("equals returns false for different name")
    void testEqualsDifferentName() {
        ManualSuiteTest suite1 = new ManualSuiteTest(1, "Suite A", "Description A");
        ManualSuiteTest suite2 = new ManualSuiteTest(1, "Suite B", "Description A");
        assertFalse(suite1.equals(suite2), "Les suites avec des noms différents ne doivent pas être égales");
    }

    @Test
    @DisplayName("equals returns false for different description")
    void testEqualsDifferentDescription() {
        ManualSuiteTest suite1 = new ManualSuiteTest(1, "Suite A", "Description A");
        ManualSuiteTest suite2 = new ManualSuiteTest(1, "Suite A", "Description B");
        assertFalse(suite1.equals(suite2), "Les suites avec des descriptions différentes ne doivent pas être égales");
    }

    @Test
    @DisplayName("equals returns false for different tests list")
    void testEqualsDifferentTests() {
        ManualSuiteTest suite1 = new ManualSuiteTest(1, "Suite A", "Description A");
        ManualSuiteTest suite2 = new ManualSuiteTest(1, "Suite A", "Description A");
        suite1.addTest(new ManualTest(1, "Test1"));
        assertFalse(suite1.equals(suite2),
                "Les suites avec des listes de tests différentes ne doivent pas être égales");
    }

    @Test
    @DisplayName("hashCode returns non-zero and consistent value")
    void testHashCode() {
        suite = new ManualSuiteTest(1, "Suite A", "Description A");
        int hash1 = suite.hashCode();
        int hash2 = suite.hashCode();
        assertNotEquals(0, hash1, "Le hashCode ne doit pas être 0");
        assertEquals(hash1, hash2, "Le hashCode doit être cohérent sur plusieurs appels");
    }

    @Test
    @DisplayName("equals: reflexivity - an object must equal itself")
    void testEqualsReflexivity() {
        ManualSuiteTest suite = new ManualSuiteTest(1, "Suite", "Description");
        // Vérification de la condition "if (this == o) return true"
        assertTrue(suite.equals(suite), "Un objet doit être égal à lui-même");
    }

    @Test
    @DisplayName("equals: should return false when comparing with an object of another type")
    void testEqualsDifferentType() {
        ManualSuiteTest suite = new ManualSuiteTest(1, "Suite", "Description");
        Object other = new Object();
        // Vérification de la condition "if (!(o instanceof ManualSuiteTest)) return
        // false"
        assertFalse(suite.equals(other), "Un objet ne doit pas être égal à un objet d'un type différent");
    }

    @Test
    @DisplayName("removeAllTests() vide bien la liste des tests")
    void testRemoveAllTests() {
        // Arrange
        ManualTest test1 = new ManualTest(1, "Test 1");
        ManualTest test2 = new ManualTest(2, "Test 2");
        suite.addTest(test1);
        suite.addTest(test2);
        assertEquals(2, suite.getTests().size(), "Pré-condition : 2 tests ajoutés");

        // Act
        suite.removeAllTests();

        // Assert
        assertTrue(suite.getTests().isEmpty(), "Après removeAllTests, la liste doit être vide");
    }

}
