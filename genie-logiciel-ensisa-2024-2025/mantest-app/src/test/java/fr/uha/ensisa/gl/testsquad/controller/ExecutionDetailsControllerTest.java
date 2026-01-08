package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution.StepExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;

public class ExecutionDetailsControllerTest {

    private ExecutionDetailsController sut;
    private long idForTesting;

    @Mock
    private DaoFactory dao;

    @Mock
    private TestDao TestDaoMock;

    @Mock
    private TestExecutionDao TestExecutionDaoMock;

    @Mock
    private ManualTest ManualTestMock;

    @Mock
    private ManualTestExecution ManualTestExecutionMock;

    @Mock
    private StepExecution stepExecutionMock1;

    @Mock
    private StepExecution stepExecutionMock2;

    @BeforeEach
    public void setUp() {
        idForTesting = 1;
        MockitoAnnotations.openMocks(this);
        sut = new ExecutionDetailsController();
        sut.setDao(dao);
        when(ManualTestExecutionMock.getSteps()).thenReturn(List.of(stepExecutionMock1, stepExecutionMock2));

    }

    @AfterEach
    public void verifyMockingInteractions() {
        // All tests call .find() via the controller logic
        verify(dao).getTestExecutionDao();
        verify(TestExecutionDaoMock).find(idForTesting);

        // Ensure no further calls on these mocks (except the ones done
        // in tests that explicitly verify update(...) or remove(...)).
        verifyNoMoreInteractions(dao);
        verifyNoMoreInteractions(TestDaoMock);
        verifyNoMoreInteractions(TestExecutionDaoMock);
    }

    @Test
    public void noExecutionFound() {
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);
        when(TestExecutionDaoMock.find(idForTesting)).thenReturn(null);

        ModelAndView result = sut.viewExecutionDetails(idForTesting);
        Object not_found_test = result.getModel().get("errorMessage");

        assertInstanceOf(String.class, not_found_test);
        assertEquals("Execution not found", not_found_test);
    }

    @Test
    public void executionViewDetailsEmptySteps() {
        ManualTestExecution executionInStore = new ManualTestExecution(idForTesting, ManualTestMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);
        when(TestExecutionDaoMock.find(idForTesting)).thenReturn(executionInStore);

        ModelAndView result = sut.viewExecutionDetails(idForTesting);
        assertEquals("Execution not found", result.getModelMap().get("errorMessage"));
    }

    @Test
    public void executionDetailsNotEmpty() {
        ManualTest testInStore = new ManualTest(idForTesting);
        String testStep1 = "This is the 1st test step.";
        String testStep2 = "This is the 2nd test step.";
        testInStore.addStep(testStep1);
        testInStore.addStep(testStep2);

        ManualTestExecution executionInStore = new ManualTestExecution(idForTesting, testInStore);

        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);
        when(TestExecutionDaoMock.find(idForTesting)).thenReturn(executionInStore);
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(TestDaoMock.find(idForTesting)).thenReturn(testInStore);

        ModelAndView result = sut.viewExecutionDetails(idForTesting);
        Object found_execution = result.getModel().get("execution");
        Object found_execution_id = result.getModel().get("id");
        Object found_execution_details = result.getModel().get("steps");

        assertInstanceOf(ManualTestExecution.class, found_execution);
        assertInstanceOf(Long.class, found_execution_id);
        assertInstanceOf(List.class, found_execution_details);
        assertFalse(((List<?>) found_execution_details).isEmpty());
        assertEquals(2, ((List<?>) found_execution_details).size());
        assertTrue(((Collection<?>) found_execution_details)
                .containsAll(List.of(executionInStore.getSteps().get(0),executionInStore.getSteps().get(1))));

        verify(dao).getTestDao();
        verify(TestDaoMock).find(idForTesting);
    }

}
