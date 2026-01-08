package fr.uha.ensisa.gl.testsquad.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;

@Controller
public class TestCreateController {

    @Autowired
    private DaoFactory dao;

    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping(value = "/testCreate")
    public ModelAndView create(
            @RequestParam(name = "embedded", required = false, defaultValue = "false") boolean embedded) {
        ModelAndView mav = new ModelAndView("testCreate");
        mav.addObject("embedded", embedded);
        return mav;
    }

    @PostMapping(value = "/testCreate")
    public ModelAndView create(
            @RequestParam(name = "testName", required = true) String testName,
            @RequestParam(name = "testStepsJson", required = true) String testStepsJson,
            @RequestParam(name = "description", required = true) String description,
            @RequestParam(name = "embedded", required = false, defaultValue = "false") boolean embedded)
            throws Exception {

        if (testName == null || testName.isEmpty()) {
            return new ModelAndView("redirect:/400");
        }
        if (description == null) {
            return new ModelAndView("redirect:/400");
        }
        if (testStepsJson == null || testStepsJson.isEmpty()) {
            return new ModelAndView("redirect:/400");
        }

        // Deserialization of JSON in List<String>
        ObjectMapper mapper = new ObjectMapper();
        List<String> stepsStr = mapper.readValue(testStepsJson, new TypeReference<List<String>>() {
        });
        // convert in objects Step
        List<ManualTest.Step> stepList = stepsStr.stream()
                .map(ManualTest.Step::new)
                .collect(Collectors.toList());
        ManualTest newTest = new ManualTest(0, testName, description, stepList);

        dao.getTestDao().persist(newTest);

        if (embedded) {
            // special view to close iframe
            ModelAndView mav = new ModelAndView("testCreateSuccess");
            mav.addObject("embedded", true);
            return mav;
        } else {
            // normal redirection
            return new ModelAndView("redirect:/testList");
        }
    }

}