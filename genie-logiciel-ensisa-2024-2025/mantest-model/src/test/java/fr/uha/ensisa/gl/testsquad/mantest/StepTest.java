package fr.uha.ensisa.gl.testsquad.mantest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;

public class StepTest {

    @Test
    @DisplayName("Test setDescription and getDescription")
    void testSetAndGetDescription() {
        Step step = new Step("Initial Desc");
        assertEquals("Initial Desc", step.getDescription());

        step.setDescription("New Desc");
        assertEquals("New Desc", step.getDescription());
    }

    @Test
    @DisplayName("Test equals(Object) same instance => true")
    void testEqualsSameInstance() {
        Step step = new Step("Desc");
        assertTrue(step.equals(step), "A Step should be equal to itself");
    }

    @Test
    @DisplayName("Test equals(Object) null => false")
    void testEqualsNull() {
        Step step = new Step("Desc");
        assertFalse(step.equals(null), "A Step should not be equal to null");
    }

    @Test
    @DisplayName("Test equals(Object) different class => false")
    void testEqualsDifferentClass() {
        Step step = new Step("Desc");
        String someOtherObject = "NotAStep";
        assertFalse(step.equals(someOtherObject), "A Step should not be equal to an object of a different class");
    }

    @Test
    @DisplayName("Test equals(Object) different description => false")
    void testEqualsDifferentDescription() {
        Step step1 = new Step("Desc1");
        step1.setStatus(StepStatus.UNDEFINED);

        Step step2 = new Step("Desc2");
        step2.setStatus(StepStatus.UNDEFINED);

        assertFalse(step1.equals(step2), "Steps with different descriptions should not be equal");
    }

    @Test
    @DisplayName("Test equals(Object) different status => false")
    void testEqualsDifferentStatus() {
        Step step1 = new Step("Desc");
        step1.setStatus(StepStatus.ACCEPTED);

        Step step2 = new Step("Desc");
        step2.setStatus(StepStatus.REFUSED);

        assertFalse(step1.equals(step2), "Steps with same description but different status should not be equal");
    }

    @Test
    @DisplayName("Test equals(Object) same description and same status => true")
    void testEqualsSameDescriptionAndStatus() {
        Step step1 = new Step("Desc");
        step1.setStatus(StepStatus.ACCEPTED);

        Step step2 = new Step("Desc");
        step2.setStatus(StepStatus.ACCEPTED);

        // Should be considered equal even if validation dates differ, because equals() ignores validationDate
        assertTrue(step1.equals(step2), "Steps with the same description and status should be equal");
    }

    @Test
    @DisplayName("Test hashCode() changes if we change Step fields")
    void testHashCode() {
        // We'll show it changes upon certain changes, though 100% coverage is about calling the code,
        // not guaranteeing any particular outcome. 
        Step step1 = new Step("Desc", StepStatus.ACCEPTED);
        int initialHash = step1.hashCode();

        // Change description
        step1.setDescription("NewDesc");
        int afterDescChange = step1.hashCode();
        assertNotEquals(initialHash, afterDescChange,
                "hashCode should typically change after altering the description");

        // Change status => sets validationDate internally
        step1.setStatus(StepStatus.REFUSED);
        int afterStatusChange = step1.hashCode();
        // Likely different, but not guaranteed, we just confirm we executed the code for coverage
        assertNotEquals(afterDescChange, afterStatusChange,
                "hashCode should generally change when status/validationDate changes");

        // For completeness, just call it again
        int afterSecondCall = step1.hashCode();
        // Usually the same (no changes in fields), but calling it covers the code again
        assertEquals(afterStatusChange, afterSecondCall, 
                "Calling hashCode repeatedly with no field changes should yield the same value");
    }

    @Test
    @DisplayName("Test toString() includes key fields")
    void testToString() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS); 
        Step step = new Step("Desc", StepStatus.ACCEPTED);

        String result = step.toString();
        // Expect something like: Step{description='Desc', status=ACCEPTED, validationDate=2023-01-01T12:00:00}
        assertTrue(result.contains("Desc"), "toString() should include the description");
        assertTrue(result.contains("ACCEPTED"), "toString() should include the status");
    }
}
