package fr.uha.ensisa.gl.testsquad.mantest.dao;

import java.util.Collection;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;

public interface TestDao{

    public void persist(ManualTest test);

    public void update(ManualTest test);

    public void remove(long id);

    public ManualTest find(long id);

    public Collection<ManualTest> findAll();

    public long count();

    public void reset();

}
