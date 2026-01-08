package fr.uha.ensisa.gl.testsquad.controller;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;

@Controller
public class TestListController {

    @Autowired
    private DaoFactory dao;

    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping(value = "/testList")
    public ModelAndView listTests() {
        ModelAndView ret = new ModelAndView("testList");
        Collection<ManualTest> allTests = dao.getTestDao().findAll();

        if (!allTests.isEmpty()){
            ret.addObject("tests", allTests);
        } else {
            ret.addObject("tests", new ArrayList<ManualTest>());
            ret.addObject("errorMessage", "No tests found");
        }
        return ret;
    }

}
