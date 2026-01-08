package fr.uha.ensisa.gl.testsquad.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;

@Controller
public class SuiteListController {

    @Autowired
    private DaoFactory dao;

    // Pour faciliter les tests unitaires
    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping(value = "/suiteList")
    public ModelAndView listSuites() {
        Collection<ManualSuiteTest> suites = dao.getSuiteDao().findAll();
        ModelAndView mav = new ModelAndView("suiteList");
        mav.addObject("suites", suites);
        return mav;
    }
}
