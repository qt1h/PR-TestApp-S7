package fr.uha.ensisa.gl.testsquad.mantest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;
import fr.uha.ensisa.gl.testsquad.mantest.status.TestStatus;

public class ManualTestExecutionTest {

    ManualTestExecution sut;

    @BeforeEach
    public void createManualTestExecution() {
        ManualTest test = new ManualTest(1, "testName", "testDescription");
        sut = new ManualTestExecution(0, test);
    }

    @Test
    public void createManualTestExecutionWithoutSteps() {
        long testId = 1;
        long executionId = 0;
        String testComment = "";

        assertNotNull(sut);
        assertEquals(executionId, sut.getId());
        assertEquals(testId, sut.getTestId());
        assertEquals(testComment, sut.getComment());
        assertEquals(0, sut.getSteps().size());
    }

    @Test
    public void createManualTestExecutionWithSteps() {
        long testId = 1;
        long executionId = 0;
        String testName = "testName";
        String testDescription = "testDescription";
        String testComment = "";

        List<Step> steps = new ArrayList<>();
        Step step1 = new Step("step1");
        step1.setStatus(StepStatus.ACCEPTED);
        Step step2 = new Step("step2");
        step2.setStatus(StepStatus.ACCEPTED);
        steps.add(step1);
        steps.add(step2);

        ManualTest test = new ManualTest(testId, testName, testDescription, steps);
        sut = new ManualTestExecution(executionId, test);

        assertNotNull(sut);
        assertEquals(executionId, sut.getId());
        assertEquals(testId, sut.getTestId());
        assertEquals(testComment, sut.getComment());
        assertEquals(steps.size(), sut.getSteps().size());
        assertNull(sut.getDate());
        assertEquals(2, sut.getSteps().size());
        assertEquals(TestStatus.PASSED, sut.getStatus());
    }

    @Test
    public void createManualTestExecutionWithStepsAndValidationDate() {
        long testId = 1;
        long executionId = 0;
        String testName = "testName";
        String testDescription = "testDescription";
        String testComment = "";
        LocalDateTime validationDate = LocalDateTime.now();

        List<Step> steps = new ArrayList<>();
        Step step1 = new Step("step1");
        step1.setStatus(StepStatus.ACCEPTED);
        Step step2 = new Step("step2");
        step2.setStatus(StepStatus.ACCEPTED);
        steps.add(step1);
        steps.add(step2);

        ManualTest test = new ManualTest(testId, testName, testDescription, steps);
        sut = new ManualTestExecution(executionId, test, validationDate);

        assertNotNull(sut);
        assertEquals(executionId, sut.getId());
        assertEquals(testId, sut.getTestId());
        assertEquals(testComment, sut.getComment());
        assertEquals(steps.size(), sut.getSteps().size());
        assertEquals(validationDate, sut.getDate());
        assertEquals(2, sut.getSteps().size());
        assertEquals(TestStatus.PASSED, sut.getStatus());
    }

    @Test
    public void testGetTest() {
        ManualTest test = new ManualTest(1, "testName", "testDescription");
        sut = new ManualTestExecution(0, test);
        assertEquals(test.getId(), sut.getTestId());
        assertEquals(test.getSteps().size(), sut.getSteps().size());
    }

    @Test
    public void testSetAndGetId() {
        sut.setId(3);
        assertEquals(3, sut.getId());
    }

    @Test
    public void testSetAndGetComment() {
        sut.setComment("comment");
        assertEquals("comment", sut.getComment());
    }

    @Test
    public void testSetAndGetValidationDate() {
        LocalDateTime date = LocalDateTime.now();
        assertNull(sut.getDate());
        sut.setDate(date);
        assertEquals(date, sut.getDate());
    }

    @Test
    public void testGetStatusEmptySteps() {
        assertEquals(0, sut.getSteps().size());
        assertEquals(TestStatus.NOT_STARTED, sut.getStatus());
    }

    @Test
    public void testGetStatusAllAccepted() {
        ManualTest test = new ManualTest(1, "testName", "testDescription");
        Step step1 = new Step("step1");
        Step step2 = new Step("step2");
        step1.setStatus(StepStatus.ACCEPTED);
        step2.setStatus(StepStatus.ACCEPTED);
        test.addStep(step1);
        test.addStep(step2);
        sut = new ManualTestExecution(0, test);
        assertEquals(2, sut.getSteps().size());
        assertEquals(TestStatus.PASSED, sut.getStatus());
    }

    @Test
    public void testGetStatusAnyRefusedFirst() {
        ManualTest test = new ManualTest(1, "testName", "testDescription");
        Step step1 = new Step("step1");
        Step step2 = new Step("step2");
        step1.setStatus(StepStatus.REFUSED);
        step2.setStatus(StepStatus.ACCEPTED);
        test.addStep(step1);
        test.addStep(step2);
        sut = new ManualTestExecution(0, test);
        assertEquals(2, sut.getSteps().size());
        assertEquals(TestStatus.FAILED, sut.getStatus());
    }

    @Test
    public void testGetStatusAnyRefusedSecond() {
        ManualTest test = new ManualTest(1, "testName", "testDescription");
        Step step1 = new Step("step1");
        Step step2 = new Step("step2");
        step1.setStatus(StepStatus.ACCEPTED);
        step2.setStatus(StepStatus.REFUSED);
        test.addStep(step1);
        test.addStep(step2);
        sut = new ManualTestExecution(0, test);
        assertEquals(2, sut.getSteps().size());
        assertEquals(TestStatus.FAILED, sut.getStatus());
    }

    @Test
    public void testGetStatusAnyInProgressNoneRefused() {
        ManualTest test = new ManualTest(1, "testName", "testDescription");
        Step step1 = new Step("step1");
        Step step2 = new Step("step2");
        step1.setStatus(StepStatus.ACCEPTED);
        step2.setStatus(StepStatus.IN_PROGRESS);
        test.addStep(step1);
        test.addStep(step2);
        sut = new ManualTestExecution(0, test);
        assertEquals(2, sut.getSteps().size());
        assertEquals(TestStatus.IN_PROGRESS, sut.getStatus());
    }

    @Test
    public void testGetStatusAllUndefined() {
        ManualTest test = new ManualTest(1, "testName", "testDescription");
        Step step1 = new Step("step1");
        Step step2 = new Step("step2");
        step1.setStatus(StepStatus.UNDEFINED);
        step2.setStatus(StepStatus.UNDEFINED);
        test.addStep(step1);
        test.addStep(step2);
        sut = new ManualTestExecution(0, test);
        assertEquals(2, sut.getSteps().size());
        assertEquals(TestStatus.NOT_STARTED, sut.getStatus());
    }
}
