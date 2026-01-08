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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SuiteCreateController {

    @Autowired
    private DaoFactory dao;

    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping("/suiteCreate/availableTests")
    public ModelAndView availableTests() {
        ModelAndView mav = new ModelAndView("../fragments/suiteCreateAvailableTests.html");

        Collection<ManualTest> tests = dao.getTestDao().findAll();
        mav.addObject("tests", tests);
        return mav;
    }

    @GetMapping("/suiteCreate")
    public ModelAndView createSuiteForm(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("suiteCreate");
        String cp = request.getContextPath();
        if (cp == null || "null".equals(cp)) {
            cp = "";
        }
        mav.addObject("contextPath", cp);
        Collection<ManualTest> tests = dao.getTestDao().findAll();
        mav.addObject("tests", tests);
        return mav;
    }
    

    @PostMapping(value = "/suiteCreate")
    public ResponseEntity<String> createSuite(
            @RequestParam(name = "suiteName", required = true) String suiteName,
            @RequestParam(name = "description", required = true) String description,
            @RequestParam(name = "suiteTestsJson", required = false) String suiteTestsJson) throws Exception {

        if (suiteName == null || suiteName.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }
        if (description == null) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        ManualSuiteTest suite = new ManualSuiteTest(0, suiteName, description);

        if (suiteTestsJson != null && !suiteTestsJson.trim().isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            List<Long> testIds = mapper.readValue(suiteTestsJson, new TypeReference<List<Long>>() {
            });
            testIds.forEach(id -> {
                ManualTest test = dao.getTestDao().find(id);
                if (test != null) {
                    suite.addTest(test);
                }
            });
        }

        dao.getSuiteDao().persist(suite);
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "./suiteList").build();
    }

}
