package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;

public class TestDaoMem implements TestDao {

    private long availableId;

    private final Map<Long, ManualTest> store = Collections.synchronizedMap(new TreeMap<Long, ManualTest>());

    TestDaoMem() {
        availableId = 1;
    }

    public long getAvailableId() {
        return availableId;
    }

    @Override
    public void persist(ManualTest test) {
        test.setId(availableId);
        store.put(test.getId(), test);
        availableId++;
    }

    @Override
    public void update(ManualTest test) {
        store.put(test.getId(), test);
    }

    @Override
    public void remove(long id) {
        store.remove(id);
    }

    @Override
    public ManualTest find(long id) {
        return store.get(id);
    }

    @Override
    public Collection<ManualTest> findAll() {
        return store.values();
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
