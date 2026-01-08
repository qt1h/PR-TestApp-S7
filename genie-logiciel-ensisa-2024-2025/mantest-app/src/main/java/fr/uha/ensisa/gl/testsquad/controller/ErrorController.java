package fr.uha.ensisa.gl.testsquad.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

    @GetMapping("/400")
    public ModelAndView error400() {
        ModelAndView model = new ModelAndView("error");

        model.addObject("title", "400 - Bad Request");
        model.addObject("error", "400");
        model.addObject("errorMessage", "Bad Request");
        model.addObject("errorSubMessage", "Oups ! The request was rejected by the server.");

        return model;
    }

    @GetMapping("/403")
    public ModelAndView error403() {
        ModelAndView model = new ModelAndView("error");

        model.addObject("title", "403 - Invalid Access");
        model.addObject("error", "403");
        model.addObject("errorMessage", "Invalid Access");
        model.addObject("errorSubMessage", "Oups ! You have no access to this page.");

        return model;
    }

    @GetMapping("/404")
    public ModelAndView error404() {
        ModelAndView model = new ModelAndView("error");

        model.addObject("title", "404 - Not Found");
        model.addObject("error", "404");
        model.addObject("errorMessage", "Page not found");
        model.addObject("errorSubMessage", "Oups ! The page doesn't exist or has been moved.");

        return model;
    }

    @GetMapping("/405")
    public ModelAndView error405() {
        ModelAndView model = new ModelAndView("error");

        model.addObject("title", "405 - Method Not Allowed");
        model.addObject("error", "405");
        model.addObject("errorMessage", "Method Not Allowed");
        model.addObject("errorSubMessage", "Oups ! The request method was not supported by the server.");

        return model;
    }

    @GetMapping("/500")
    public ModelAndView error500() {
        ModelAndView model = new ModelAndView("error");

        model.addObject("title", "500 - Internal Server Error");
        model.addObject("error", "500");
        model.addObject("errorMessage", "Internal Server Error");
        model.addObject("errorSubMessage", "Oups ! Something went wrong on our side.");

        return model;
    }


}


