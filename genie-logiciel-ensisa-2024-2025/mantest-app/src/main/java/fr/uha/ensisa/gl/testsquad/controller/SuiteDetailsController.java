package fr.uha.ensisa.gl.testsquad.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;

@Controller
public class SuiteDetailsController {

    @Autowired
    private DaoFactory dao;

    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping(value = "/suiteDetails")
    public ModelAndView viewSuiteDetails(@RequestParam(name = "id") long id) {
        ManualSuiteTest suite = dao.getSuiteDao().find(id);
        ModelAndView mav = new ModelAndView("suiteDetails");

        if (suite != null) {
            Collection<ManualTest> allTests = dao.getTestDao().findAll();
            List<ManualTest> suiteTests = suite.getTests();

            // Exclure les tests déjà dans la suite
            List<ManualTest> availableTests = allTests.stream()
                    .filter(t -> !suiteTests.contains(t))
                    .toList();

            mav.addObject("suite", suite);
            mav.addObject("tests", suiteTests);
            mav.addObject("availableTests", availableTests);
        } else {
            mav.addObject("errorMessage", "Suite not found");
        }

        return mav;
    }

    @PostMapping(value = "/removeSuite")
    public ResponseEntity<String> deleteSuite(@RequestParam(name = "id") long id) {
        SuiteTestDao suiteTestDao = dao.getSuiteDao();
        ManualSuiteTest suite = suiteTestDao.find(id);
        if (suite != null) {
            suiteTestDao.remove(id);
            // dao.getTestExecutionDao().removeByTestId(id);
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./suiteList").build();
        } else {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./404").build();
        }
    }

    @PostMapping(value = "/updateSuite")
    public ResponseEntity<String> updateSuite(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "testIds", required = false) List<Long> testIds) {

        SuiteTestDao suiteTestDao = dao.getSuiteDao();

        ManualSuiteTest suiteTest = suiteTestDao.find(id);
        if (suiteTest == null) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./404").build();
        }

        if (name == null || description == null || name.strip().isEmpty()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        suiteTest.setName(name.strip());
        suiteTest.setDescription(description.strip());
        suiteTest.removeAllTests();

        if (testIds != null) {
            for (Long testId : testIds) {
                ManualTest test = dao.getTestDao().find(testId);
                if (test != null) {
                    suiteTest.addTest(test);
                }
            }
        }

        suiteTestDao.update(suiteTest);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "./suiteDetails?id=" + id).build();
    }
}
