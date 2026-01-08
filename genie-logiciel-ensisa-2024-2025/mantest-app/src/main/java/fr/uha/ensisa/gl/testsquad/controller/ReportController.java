package fr.uha.ensisa.gl.testsquad.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;
import fr.uha.ensisa.gl.testsquad.mantest.status.TestStatus;

@Controller
public class ReportController {

    @Autowired
    private DaoFactory dao;

    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping(value = "/report")
    public ModelAndView report() {
        ModelAndView ret = new ModelAndView("report");

        TestDao testDao = dao.getTestDao();
        TestExecutionDao executionDao = dao.getTestExecutionDao();


        Collection<ManualTestExecution> allExecutions = executionDao.findAll();
        Collection<ManualTest> allTests = testDao.findAll();

        Collection<ManualTest> testsWithExecutions = new ArrayList<>();
        Collection<ManualTestExecution> latestExecutions = new ArrayList<>();

        List<ManualTest> sortedTests = new ArrayList<>(allTests);

        List<TestStatus> customOrder = Arrays.asList(
                TestStatus.REGRESSION,
                TestStatus.FAILED,
                TestStatus.NOT_STARTED,
                TestStatus.IN_PROGRESS,
                TestStatus.PASSED
        );

        sortedTests.sort(Comparator.comparingInt(test -> customOrder.indexOf(test.getStatus())));

        for (ManualTest test : sortedTests) {
            ManualTestExecution lastExecution = null;

            for (ManualTestExecution execution : allExecutions) {
                if (execution.getTestId() == test.getId()) {
                    if (lastExecution == null || execution.getDate().isAfter(lastExecution.getDate())) {
                        lastExecution = execution;
                    }
                }
            }

            if (lastExecution != null) {
                testsWithExecutions.add(test);
                latestExecutions.add(lastExecution);
            }
        }

        if (!testsWithExecutions.isEmpty()) {
            ret.addObject("tests", testsWithExecutions);
            ret.addObject("latest_executions", latestExecutions);
        } else {
            ret.addObject("tests", new ArrayList<>());
            ret.addObject("latest_executions", new ArrayList<>());
            ret.addObject("errorMessage", "No test with executions found");
        }
        return ret;
    }

    @GetMapping(value = "/testReport")
    public ModelAndView testReport(@RequestParam(name = "id") long id) {
        ModelAndView ret = new ModelAndView("testReport");

        TestDao testDao = dao.getTestDao();
        TestExecutionDao executionDao = dao.getTestExecutionDao();

        ManualTest test = testDao.find(id);
        Collection<ManualTestExecution> allExecutions = executionDao.findAll();

        List<ManualTestExecution> sortedExecutions = new ArrayList<>();

        if (test != null) {
            for (ManualTestExecution execution : allExecutions) {
                if (execution.getTestId() == id) {
                    sortedExecutions.add(execution);
                }
            }
        } else {
            ret.addObject("executions", new ArrayList<>());
            ret.addObject("errorMessage", "Test not found");
            return ret;
        }

        sortedExecutions.sort((e1, e2) -> e2.getDate().compareTo(e1.getDate()));

        if (!sortedExecutions.isEmpty()) {
            ret.addObject("test", test);
            ret.addObject("executions", sortedExecutions);
        } else {
            ret.addObject("test", test);
            ret.addObject("executions", new ArrayList<>());
            ret.addObject("errorMessage", "No test with executions found");
        }
        return ret;
    }
}
