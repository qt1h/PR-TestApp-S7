package fr.uha.ensisa.gl.testsquad.mantest.dao;

public interface DaoFactory {

    public TestDao getTestDao();

    public TestExecutionDao getTestExecutionDao();

    public SuiteTestDao getSuiteDao();

    public void reset();
}
