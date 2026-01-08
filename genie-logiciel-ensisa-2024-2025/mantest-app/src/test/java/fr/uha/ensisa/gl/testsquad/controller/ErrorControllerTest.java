package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ErrorControllerTest {

    private ErrorController sut;

    @BeforeEach
    public void setUp() {
        sut = new ErrorController();
    }

    @Test
    public void testError400() {
        ModelAndView modelAndView = sut.error400();

        assertEquals("400 - Bad Request", modelAndView.getModelMap().get("title"));
        assertEquals("400", modelAndView.getModelMap().get("error"));
        assertEquals("Bad Request", modelAndView.getModelMap().get("errorMessage"));
        assertEquals("Oups ! The request was rejected by the server.", modelAndView.getModelMap().get("errorSubMessage"));

        assertNotNull(modelAndView);
    }

    @Test
    public void testError403() {
        ModelAndView modelAndView = sut.error403();

        assertEquals("403 - Invalid Access", modelAndView.getModelMap().get("title"));
        assertEquals("403", modelAndView.getModelMap().get("error"));
        assertEquals("Invalid Access", modelAndView.getModelMap().get("errorMessage"));
        assertEquals("Oups ! You have no access to this page.", modelAndView.getModelMap().get("errorSubMessage"));

        assertNotNull(modelAndView);
    }

    @Test
    public void testError404() {
        ModelAndView modelAndView = sut.error404();

        assertEquals("404 - Not Found", modelAndView.getModelMap().get("title"));
        assertEquals("404", modelAndView.getModelMap().get("error"));
        assertEquals("Page not found", modelAndView.getModelMap().get("errorMessage"));
        assertEquals("Oups ! The page doesn't exist or has been moved.", modelAndView.getModelMap().get("errorSubMessage"));

        assertNotNull(modelAndView);
    }

    @Test
    public void testError405() {
        ModelAndView modelAndView = sut.error405();

        assertEquals("405 - Method Not Allowed", modelAndView.getModelMap().get("title"));
        assertEquals("405", modelAndView.getModelMap().get("error"));
        assertEquals("Method Not Allowed", modelAndView.getModelMap().get("errorMessage"));
        assertEquals("Oups ! The request method was not supported by the server.", modelAndView.getModelMap().get("errorSubMessage"));

        assertNotNull(modelAndView);
    }

    @Test
    public void testError500() {
        ModelAndView modelAndView = sut.error500();

        assertEquals("500 - Internal Server Error", modelAndView.getModelMap().get("title"));
        assertEquals("500", modelAndView.getModelMap().get("error"));
        assertEquals("Internal Server Error", modelAndView.getModelMap().get("errorMessage"));
        assertEquals("Oups ! Something went wrong on our side.", modelAndView.getModelMap().get("errorSubMessage"));

        assertNotNull(modelAndView);
    }
}
