package fr.uha.ensisa.gl.testsquad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController{

    @Autowired
    private DaoFactory dao;

    void setDao(DaoFactory dao){
        this.dao = dao;
    }

    @PostMapping(value = "/reset")
    public ResponseEntity<String> reset() {
        dao.reset();
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "./home").build();
    }

    @GetMapping(value = "/admin")
    public ModelAndView admin() {
        return new ModelAndView("admin");
    }

}
