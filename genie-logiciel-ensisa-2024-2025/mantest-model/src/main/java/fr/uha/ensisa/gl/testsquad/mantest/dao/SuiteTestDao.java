package fr.uha.ensisa.gl.testsquad.mantest.dao;

import java.util.Collection;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;

public interface SuiteTestDao {

    public void persist(ManualSuiteTest suite);

    public void update(ManualSuiteTest suite);

    public void remove(long id);

    public ManualSuiteTest find(long id);

    public Collection<ManualSuiteTest> findAll();

    public long count();

    public void reset();
}
