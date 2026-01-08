package fr.uha.ensisa.gl.testsquad.mantest.dao.mem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;

public class TestExecutionDaoMem implements TestExecutionDao {

    private long availableId;

    private final Map<Long, ManualTestExecution> store = Collections
            .synchronizedMap(new TreeMap<Long, ManualTestExecution>());

    public TestExecutionDaoMem() {
        availableId = 1;
    }

    public long getAvailableId() {
        return availableId;
    }

    @Override
    public void persist(ManualTestExecution test) {
        test.setId(availableId);
        store.put(test.getId(), test);
        availableId++;
    }

    @Override
    public void update(ManualTestExecution test) {
        store.put(test.getId(), test);
    }

    @Override
    public void remove(long id) {
        store.remove(id);
    }

    @Override
    public void removeByTestId(long testId) {
        ArrayList<Long> toRemove = new ArrayList<Long>();
        for (ManualTestExecution manualStepTestExecution : store.values()) {
            if (manualStepTestExecution.getTestId() == testId) {
                toRemove.add(manualStepTestExecution.getId());
            }
        }
        for (Long id : toRemove) {
            store.remove(id);
        }
    }

    @Override
    public ManualTestExecution find(long id) {
        return store.get(id);
    }

    @Override
    public Collection<ManualTestExecution> findAll() {
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
