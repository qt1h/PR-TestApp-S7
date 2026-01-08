package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DaoFactoryMemTest {

    private DaoFactoryMem sut;

    @Mock
    private TestDao testDAOMock;

    @Mock
    private TestExecutionDao testExecutionDAOMock;

    @Mock
    private SuiteTestDao suiteDaoMock;

    @BeforeEach
    public void createDaoFactoryMem() {
        MockitoAnnotations.openMocks(this);
        sut = new DaoFactoryMem();
    }

    @Test
    @DisplayName("A DaoFactory must generate a TestDao")
    public void getTestDao() {
        TestDao dao = sut.getTestDao();
        assertNotNull(dao, "TestDao should not be null");
        assertEquals(TestDaoMem.class, dao.getClass(), "Factory should return a TestDaoMem instance");
    }

    @Test
    @DisplayName("A DaoFactory must generate a TestExecutionDao")
    public void getTestExecutionDao() {
        TestExecutionDao dao = sut.getTestExecutionDao();
        assertNotNull(dao, "TestExecutionDao should not be null");
        assertEquals(TestExecutionDaoMem.class, dao.getClass(), "Factory should return a TestExecutionDaoMem instance");
    }
    
    @Test
    @DisplayName("A DaoFactory must generate a SuiteTestDao")
    public void getSuiteTestDao() {
        SuiteTestDao dao = sut.getSuiteDao();
        assertNotNull(dao, "SuiteTestDao should not be null");
        assertEquals(SuiteTestDaoMem.class, dao.getClass(), "Factory should return a SuiteTestDaoMem instance");
    }

    @Test
    public void resetDao() {
        ManualTest manualTest = new ManualTest(1, "test", "testdesc");
        ManualTestExecution manualTestExecution = new ManualTestExecution(1, manualTest);
        ManualSuiteTest manualSuiteTest = new ManualSuiteTest(1, "testsuite", "suitedesc");
        sut.getTestDao().persist(manualTest);
        sut.getTestExecutionDao().persist(manualTestExecution);
        sut.getSuiteDao().persist(manualSuiteTest);

        assertEquals(1, sut.getTestDao().count());
        assertEquals(1, sut.getTestExecutionDao().count());
        assertEquals(1, sut.getSuiteDao().count());

        sut.reset();

        assertEquals(0, sut.getTestDao().count());
        assertEquals(0, sut.getTestExecutionDao().count());
        assertEquals(0, sut.getSuiteDao().count());

    }


}
