package fr.uha.ensisa.gl.testsquad.mantest.dao;

import java.util.Collection;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;

public interface TestExecutionDao {

    public void persist(ManualTestExecution execution);

    public void update(ManualTestExecution execution);

    public void remove(long id);

    public void removeByTestId(long testId);

    public ManualTestExecution find(long id);

    public Collection<ManualTestExecution> findAll();

    public long count();

    public void reset();

}
