package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

public class HomeControllerTest {

    @Test
    public void testIndex() {
        HomeController sut = new HomeController();
        ResponseEntity<String> response = sut.index();
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
    }

    @Test
    public void testHome() {
        HomeController sut = new HomeController();
        ModelAndView result = sut.home();
        assertNotNull(result);
        assertEquals("home", result.getViewName());
    }
}
