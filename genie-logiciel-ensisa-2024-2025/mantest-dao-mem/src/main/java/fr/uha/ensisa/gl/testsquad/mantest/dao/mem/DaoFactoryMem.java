package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;

public class DaoFactoryMem implements DaoFactory {

    private final TestDao testDao = new TestDaoMem();
    private final TestExecutionDao testExecutionDao = new TestExecutionDaoMem();
    private final SuiteTestDao suiteDao = new SuiteTestDaoMem();

    @Override
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Les DAOmem sont statefull et leur exposition ne présente pas de risque.")
    public TestDao getTestDao() {
        return testDao;
    }

    @Override
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Les DAOmem sont statefull et leur exposition ne présente pas de risque.")
    public TestExecutionDao getTestExecutionDao() {
        return testExecutionDao;
    }

    @Override
    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Les DAOmem sont statefull et leur exposition ne présente pas de risque.")
    public SuiteTestDao getSuiteDao() {
        return suiteDao;
    }

    @Override
    public void reset() {
        testExecutionDao.reset();
        suiteDao.reset();
        testDao.reset();
    }
}
