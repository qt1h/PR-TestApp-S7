package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;

public class ReportControllerTest {

    ReportController sut;

    private long idForTesting;

    @Mock
    private DaoFactory dao;

    @Mock
    private TestDao TestDaoMock;

    @Mock
    private TestExecutionDao TestExecutionDaoMock;

    @Mock
    private ManualTest ManualTestMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        idForTesting = 1;
        sut = new ReportController();
        sut.setDao(dao);
    }

    @Test
    public void testReportNoTest() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        when(TestDaoMock.findAll()).thenReturn(List.of());
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of());

        ModelAndView result = sut.report();

        Object latestExecutions = result.getModelMap().get("latest_executions");
        Object test = result.getModelMap().get("tests");
        assertNotNull(latestExecutions);
        assertNotNull(test);
        assert(((List<?>) latestExecutions).isEmpty());
        assert(((List<?>) test).isEmpty());
        assertEquals("No test with executions found", result.getModelMap().get("errorMessage"));
    }

    @Test
    public void testReportTestNoExecution() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        when(TestDaoMock.findAll()).thenReturn(List.of(ManualTestMock));
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of());

        ModelAndView result = sut.report();

        Object latestExecutions = result.getModelMap().get("latest_executions");
        Object test = result.getModelMap().get("tests");
        assertNotNull(latestExecutions);
        assertNotNull(test);
        assert(((List<?>) latestExecutions).isEmpty());
        assert(((List<?>) test).isEmpty());
        assertEquals("No test with executions found", result.getModelMap().get("errorMessage"));
    }

    @Test
    public void testReportManualTestUniqueExecution() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        ManualTest storedTest = new ManualTest(1);
        ManualTestExecution storedExecution = new ManualTestExecution(1, storedTest, LocalDateTime.now());

        when(TestDaoMock.findAll()).thenReturn(List.of(storedTest));
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of(storedExecution));

        ModelAndView result = sut.report();

        Object latestExecutions = result.getModelMap().get("latest_executions");
        Object tests = result.getModelMap().get("tests");
        assertNotNull(latestExecutions);
        assertNotNull(tests);
        assertEquals(1, ((List<?>) latestExecutions).size());
        assertEquals(1, ((List<?>) tests).size());
        assertTrue(((List<?>) tests).contains(storedTest));
        ManualTestExecution realExecution = (ManualTestExecution) ((List<?>) latestExecutions).get(0);
        assertEquals(storedExecution.getId(), realExecution.getId());
    }


    @Test
    public void testReportManualTwoTestUniqueExecution() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        ManualTest storedTest = new ManualTest(1);
        ManualTest storedTest2 = new ManualTest(2);
        ManualTestExecution storedExecution = new ManualTestExecution(1, storedTest, LocalDateTime.now());
        ManualTestExecution storedExecution2 = new ManualTestExecution(2, storedTest2, LocalDateTime.now());

        when(TestDaoMock.findAll()).thenReturn(List.of(storedTest, storedTest2));
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of(storedExecution, storedExecution2));

        ModelAndView result = sut.report();

        Object latestExecutions = result.getModelMap().get("latest_executions");
        Object tests = result.getModelMap().get("tests");
        assertNotNull(latestExecutions);
        assertNotNull(tests);
        assertEquals(2, ((List<?>) latestExecutions).size());
        assertEquals(2, ((List<?>) tests).size());
        assertTrue(((List<?>) tests).containsAll(List.of(storedTest, storedTest2)));
        ManualTestExecution realExecution = (ManualTestExecution) ((List<?>) latestExecutions).get(0);
        ManualTestExecution realExecution2 = (ManualTestExecution) ((List<?>) latestExecutions).get(1);
        assertEquals(storedExecution.getId(), realExecution.getId());
        assertEquals(storedExecution2.getId(), realExecution2.getId());

    }

    @Test
    public void testReportManualTestMultipleExecutionsInOrder() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        ManualTest storedTest = new ManualTest(1);
        ManualTestExecution storedExecution = new ManualTestExecution(1, storedTest, LocalDateTime.now());
        ManualTestExecution storedExecution2 = new ManualTestExecution(2, storedTest, LocalDateTime.now());

        when(TestDaoMock.findAll()).thenReturn(List.of(storedTest));
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of(storedExecution, storedExecution2));

        ModelAndView result = sut.report();

        Object latestExecutions = result.getModelMap().get("latest_executions");
        Object tests = result.getModelMap().get("tests");
        assertNotNull(latestExecutions);
        assertNotNull(tests);
        assertEquals(1, ((List<?>) latestExecutions).size());
        assertEquals(1, ((List<?>) tests).size());
        assertTrue(((List<?>) tests).contains(storedTest));
        ManualTestExecution realExecution = (ManualTestExecution) ((List<?>) latestExecutions).get(0);
        assertEquals(storedExecution2.getId(), realExecution.getId());
    }

    @Test
    public void testReportManualTestMultipleExecutionsNotInOrder() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        ManualTest storedTest = new ManualTest(1);
        ManualTestExecution storedExecution = new ManualTestExecution(1, storedTest, LocalDateTime.now());
        ManualTestExecution storedExecution2 = new ManualTestExecution(2, storedTest, LocalDateTime.now());

        when(TestDaoMock.findAll()).thenReturn(List.of(storedTest));
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of(storedExecution2, storedExecution));

        ModelAndView result = sut.report();

        Object latestExecutions = result.getModelMap().get("latest_executions");
        Object tests = result.getModelMap().get("tests");
        assertNotNull(latestExecutions);
        assertNotNull(tests);
        assertEquals(1, ((List<?>) latestExecutions).size());
        assertEquals(1, ((List<?>) tests).size());
        assertTrue(((List<?>) tests).contains(storedTest));
        ManualTestExecution realExecution = (ManualTestExecution) ((List<?>) latestExecutions).get(0);
        assertEquals(storedExecution2.getId(), realExecution.getId());
    }

    /*
    Test pour test Report
     */

    @Test
    public void testReportIdNoTest() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        when(TestDaoMock.find(idForTesting)).thenReturn(null);
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of());

        ModelAndView result = sut.testReport(idForTesting);

        Object executions = result.getModelMap().get("executions");
        Object test = result.getModelMap().get("test");
        assertNotNull(executions);
        assert(((List<?>) executions).isEmpty());
        assertNull(test);
        assertEquals("Test not found", result.getModelMap().get("errorMessage"));
    }

    @Test
    public void testReportIdTestNoExecution() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of());

        ModelAndView result = sut.testReport(idForTesting);

        Object executions = result.getModelMap().get("executions");
        Object test = result.getModelMap().get("test");
        assertNotNull(executions);
        assertNotNull(test);
        assert(((List<?>) executions).isEmpty());
        assertEquals(ManualTestMock, test);
        assertEquals("No test with executions found", result.getModelMap().get("errorMessage"));
    }

    @Test
    public void testReportIdTestUniqueExecution() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        ManualTest storedTest = new ManualTest(1);
        ManualTest storedTest2 = new ManualTest(2);
        ManualTestExecution storedExecution = new ManualTestExecution(1, storedTest, LocalDateTime.now());
        ManualTestExecution storedExecution2 = new ManualTestExecution(2, storedTest2, LocalDateTime.now());

        when(TestDaoMock.find(idForTesting)).thenReturn(storedTest);
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of(storedExecution, storedExecution2));

        ModelAndView result = sut.testReport(idForTesting);

        Object executions = result.getModelMap().get("executions");
        Object test = result.getModelMap().get("test");
        assertNotNull(executions);
        assertNotNull(test);
        assertEquals(storedTest, test);
        ManualTestExecution realExecution = (ManualTestExecution) ((List<?>) executions).get(0);
        assertEquals(storedExecution.getId(), realExecution.getId());
    }

    @Test
    public void testReportIdTestMultipleExecutionNotInOrder() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        ManualTest storedTest = new ManualTest(1);
        ManualTestExecution storedExecution = new ManualTestExecution(1, storedTest, LocalDateTime.now());
        ManualTestExecution storedExecution2 = new ManualTestExecution(2, storedTest, LocalDateTime.now());

        when(TestDaoMock.find(idForTesting)).thenReturn(storedTest);
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of(storedExecution, storedExecution2));

        ModelAndView result = sut.testReport(idForTesting);

        Object executions = result.getModelMap().get("executions");
        Object test = result.getModelMap().get("test");
        assertNotNull(executions);
        assertNotNull(test);
        assertEquals(storedTest, test);
        ManualTestExecution realExecution = (ManualTestExecution) ((List<?>) executions).get(0);
        ManualTestExecution realExecution2 = (ManualTestExecution) ((List<?>) executions).get(1);
        assertEquals(storedExecution2.getId(), realExecution.getId());
        assertEquals(storedExecution.getId(),realExecution2.getId());

    }

    @Test
    public void testReportVerifySort() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);

        ManualTest storedTest = new ManualTest(0, "testName", "testDescription");
        ManualTest storedTest2 = new ManualTest(1, "testName2", "testDescription2");
        ManualTest storedTest3 = new ManualTest(2, "testName3", "testDescription3");
        ManualTest storedTest4 = new ManualTest(3, "testName4", "testDescription4");
        ManualTest storedTest5 = new ManualTest(4, "testName5", "testDescription5");
        Step step = new Step("desc", StepStatus.ACCEPTED);
        Step step2 = new Step("desc2", StepStatus.ACCEPTED);
        Step step3 = new Step("desc3", StepStatus.UNDEFINED);
        Step step4 = new Step("desc4", StepStatus.REFUSED);
        Step step5 = new Step("desc5", StepStatus.IN_PROGRESS);
        storedTest.addStep(step);
        storedTest2.addStep(step2);
        storedTest3.addStep(step3);
        storedTest4.addStep(step4);
        storedTest5.addStep(step5);

        storedTest.getStatus();
        storedTest.getSteps().get(0).setStatus(StepStatus.REFUSED);
        ManualTestExecution storedExecution = new ManualTestExecution(0, storedTest, LocalDateTime.now());
        storedExecution.setComment("commentForStepTestExecution");

        ManualTestExecution storedExecution2 = new ManualTestExecution(1, storedTest2, LocalDateTime.now());
        storedExecution2.setComment("commentForStepTestExecution2");

        ManualTestExecution storedExecution3 = new ManualTestExecution(2, storedTest3, LocalDateTime.now());
        storedExecution3.setComment("commentForStepTestExecution3");

        ManualTestExecution storedExecution4 = new ManualTestExecution(3, storedTest4, LocalDateTime.now());
        storedExecution4.setComment("commentForStepTestExecution4");

        ManualTestExecution storedExecution5 = new ManualTestExecution(4, storedTest5, LocalDateTime.now());
        storedExecution5.setComment("commentForStepTestExecution5");

        when(TestDaoMock.findAll()).thenReturn(List.of(storedTest, storedTest2, storedTest3, storedTest4, storedTest5));
        when(TestExecutionDaoMock.findAll()).thenReturn(List.of(storedExecution, storedExecution2, storedExecution3, storedExecution4, storedExecution5));

        ModelAndView result = sut.report();

        Object latestExecutions = result.getModelMap().get("latest_executions");
        Object tests = result.getModelMap().get("tests");
        assertNotNull(latestExecutions);
        assertNotNull(tests);
        assertEquals(5, ((List<?>) latestExecutions).size());
        assertEquals(5, ((List<?>) tests).size());
        assertTrue(((List<?>) tests).containsAll(List.of(storedTest, storedTest2, storedTest3 ,storedTest4)));

        ManualTestExecution realExecution = (ManualTestExecution) ((List<?>) latestExecutions).get(0);
        ManualTestExecution realExecution2 = (ManualTestExecution) ((List<?>) latestExecutions).get(1);
        ManualTestExecution realExecution3 = (ManualTestExecution) ((List<?>) latestExecutions).get(2);
        ManualTestExecution realExecution4 = (ManualTestExecution) ((List<?>) latestExecutions).get(3);
        ManualTestExecution realExecution5 = (ManualTestExecution) ((List<?>) latestExecutions).get(4);

        assertEquals(storedExecution.getId(), realExecution.getId());
        assertEquals(storedExecution4.getId(), realExecution2.getId());
        assertEquals(storedExecution3.getId(), realExecution3.getId());
        assertEquals(storedExecution5.getId(), realExecution4.getId());
        assertEquals(storedExecution2.getId(), realExecution5.getId());
    }
}
