package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution.StepExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.mem.DaoFactoryMem;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;
import fr.uha.ensisa.gl.testsquad.mantest.status.TestStatus;

public class ExecutionCreateControllerTest {

    private ExecutionCreateController sut;
    private long idForTesting;

    @Mock
    private ManualTest manualTestMock;

    @Mock
    private DaoFactory dao;

    @Mock
    private TestDao TestDAOMock;

    @Mock
    private TestExecutionDao TestExecutionDAOMock;

    @BeforeEach
    public void setUp() {
        sut = new ExecutionCreateController();
        idForTesting = 1;
        MockitoAnnotations.openMocks(this);
        sut.setDao(dao);
    }

    @AfterEach
    public void verifyMockingInteractions() {
        verify(dao).getTestDao();
        verify(TestDAOMock).find(idForTesting);

        verifyNoMoreInteractions(dao);

        verifyNoMoreInteractions(TestDAOMock);
        verifyNoMoreInteractions(TestExecutionDAOMock);
    }

    @Test
    public void testExecutionCreateNoTest() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(TestDAOMock.find(idForTesting)).thenReturn(null);

        ModelAndView result = sut.viewExecutionCreate(idForTesting);

        Object not_found_test = result.getModel().get("errorMessage");

        assertInstanceOf(String.class, not_found_test);
        assertEquals("Test not found", not_found_test);
    }

    @Test
    public void testExecutionCreateTestNoStep() {
        ManualTest testInStore = new ManualTest(1, "test", "description");

        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(TestDAOMock.find(idForTesting)).thenReturn(testInStore);

        ModelAndView result = sut.viewExecutionCreate(idForTesting);

        Object not_found_test = result.getModel().get("errorMessage");

        assertInstanceOf(String.class, not_found_test);
        assertEquals("Test not found", not_found_test);
    }

    @Test
    public void testExecutionCreateTest() {
        ManualTest testInStore = new ManualTest(1, "test", "description");
        Step stepInStore = new Step("description");
        testInStore.addStep(stepInStore);

        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(TestDAOMock.find(idForTesting)).thenReturn(testInStore);

        ModelAndView result = sut.viewExecutionCreate(idForTesting);

        Object id = result.getModel().get("id");
        Object test = result.getModel().get("test");
        Object steps = result.getModel().get("steps");
        Object stepsCount = result.getModel().get("stepsCount");

        assertInstanceOf(Long.class, id);
        assertInstanceOf(ManualTest.class, test);
        assertInstanceOf(List.class, steps);
        assertInstanceOf(Integer.class, stepsCount);

        assertEquals(idForTesting, id);
        assertEquals(testInStore, test);
        assertFalse(((List<?>) steps).isEmpty());
        assertEquals(1, ((List<?>) steps).size());
        assertEquals(1, stepsCount);

        Step realStep = (Step) ((List<?>) steps).get(0);
        assertEquals(stepInStore, realStep);
    }


    /*
        Tests pour Execution Create POST API
     */
    @Test
    public void testExecutionCreateTestNoTest() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(null);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, null, null, null);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkCommentNull() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, null, null, null);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepStatusesNull() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", null, null);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepStatusesEmpty() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of(), null);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepStatusesStringEmpty() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("","ACCEPTED"), null);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepStatusesStringNull() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", java.util.Arrays.asList(null,"ACCEPTED"), null);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepCommentsNull() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED","ACCEPTED"), null);

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepCommentsEmpty() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED","ACCEPTED"), List.of());

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepStringNull() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED","ACCEPTED"), java.util.Arrays.asList("Comment", null));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestOkStepCommentsStatusesSizeMismatch() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED","ACCEPTED"), List.of("Comment"));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestStepNull() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);
        when(manualTestMock.getSteps()).thenReturn(null);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED"), List.of("Comment"));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestStepEmpty() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);
        when(manualTestMock.getSteps()).thenReturn(List.of());

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED"), List.of("Comment"));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestStepSizeMismatch() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);
        when(manualTestMock.getSteps()).thenReturn(List.of(new Step("step1"), new Step("step2")));

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED"), List.of("Comment"));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestStepStatusInvalid() {
        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);
        when(manualTestMock.getSteps()).thenReturn(List.of(new Step("step")));

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("Invalid"), List.of("Comment"));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
    }

    @Test
    public void testExecutionCreateTestWorking() {
        Step stepInStore = new Step("step");

        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);
        when(manualTestMock.getSteps()).thenReturn(List.of(stepInStore));

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED"), List.of("Comment"));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
        verify(manualTestMock).removeAllSteps();
        verify(manualTestMock).addSteps(anyList());
        verify(TestDAOMock).update(manualTestMock);
        verify(TestExecutionDAOMock).persist(any());
    }

    @Test
    public void testExecutionCreateTestWorkingBis() {
        ManualTest testInStore = new ManualTest(idForTesting, "name", "description");
        Step stepInStore = new Step("step");
        testInStore.addStep(stepInStore);

        DaoFactory daoFactory = new DaoFactoryMem();
        TestExecutionDao stepTestExecutionDao = daoFactory.getTestExecutionDao();

        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(stepTestExecutionDao);

        when(TestDAOMock.find(idForTesting)).thenReturn(testInStore);

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED"), List.of("Step_Comment"));

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
        verify(TestDAOMock).update(testInStore);

        ManualTestExecution executionInStore = stepTestExecutionDao.find(idForTesting);
        StepExecution stepExecutionInStore = executionInStore.getSteps().get(0);

        assertEquals(StepStatus.ACCEPTED, testInStore.getSteps().get(0).getStatus());
        assertEquals(1, stepTestExecutionDao.count());
        assertEquals(idForTesting, executionInStore.getTestId());
        assertEquals("Comment", executionInStore.getComment());
        assertEquals(TestStatus.PASSED, executionInStore.getStatus());
        assertEquals("Step_Comment", stepExecutionInStore.getComment());
        assertEquals(StepStatus.ACCEPTED, stepExecutionInStore.getStatus());

    }

    @Test
    public void testExecutionCreateTestWorkingNoCommentOneStep() {
        Step stepInStore = new Step("step");

        when(dao.getTestDao()).thenReturn(TestDAOMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDAOMock);

        when(TestDAOMock.find(idForTesting)).thenReturn(manualTestMock);
        when(manualTestMock.getSteps()).thenReturn(List.of(stepInStore));

        ResponseEntity<String> response = sut.viewExecutionCreate(idForTesting, "Comment", List.of("ACCEPTED"), List.of());

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).getTestExecutionDao();
        verify(manualTestMock).removeAllSteps();
        verify(manualTestMock).addSteps(anyList());
        verify(TestDAOMock).update(manualTestMock);
        verify(TestExecutionDAOMock).persist(any());
    }

}
