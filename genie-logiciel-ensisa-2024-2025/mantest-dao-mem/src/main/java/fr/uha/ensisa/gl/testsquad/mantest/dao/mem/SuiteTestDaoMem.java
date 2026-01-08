package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;

public class SuiteTestDaoMem implements SuiteTestDao {

    private long availableId;
    private final Map<Long, ManualSuiteTest> store = Collections.synchronizedMap(new TreeMap<>());

    public SuiteTestDaoMem() {
        availableId = 1;
    }

    @Override
    public void persist(ManualSuiteTest suite) {
        suite.setId(availableId);
        store.put(availableId, suite);
        availableId++;
    }

    @Override
    public void update(ManualSuiteTest suite) {
        store.put(suite.getId(), suite);
    }

    @Override
    public void remove(long id) {
        store.remove(id);
    }

    @Override
    public ManualSuiteTest find(long id) {
        return store.get(id);
    }

    @Override
    public Collection<ManualSuiteTest> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public long count() {
        return store.size();
    }

    @Override
    public void reset() {
        store.clear();
        availableId = 1;
    }
}
