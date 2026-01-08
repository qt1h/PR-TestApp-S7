package fr.uha.ensisa.gl.testsquad.mantest;

import static fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus.REFUSED;
import static fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus.UNDEFINED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution.StepExecution;

public class StepExecutionTest {

    StepExecution sut;

    @Test
    public void testStepExecution() {
        String description = "Description";
        Step step = new Step(description);
        sut = new StepExecution(step);

        assertNotNull(sut);
        assertEquals(description, sut.getDescription());
        assertEquals("", sut.getComment());
        assertEquals(UNDEFINED, sut.getStatus());
    }

    @Test
    public void testSetAndGetComment() {
        String description = "Verifier la présence du bouton de connection sur la page";
        String comment = "Le bouton n'apparait pas quand l'utilisateur veut se connecter";
        Step step = new Step(description);
        sut = new StepExecution(step, REFUSED);

        assertNotNull(sut);
        sut.setComment(comment);
        assertEquals(comment, sut.getComment());
    }

    @Test
    public void testGetStep() {
        String description = "Description";
        Step step = new Step(description);
        sut = new StepExecution(step);

        assertNotNull(sut.getStep());
        assertEquals(description, sut.getStep().getDescription());
    }
}
