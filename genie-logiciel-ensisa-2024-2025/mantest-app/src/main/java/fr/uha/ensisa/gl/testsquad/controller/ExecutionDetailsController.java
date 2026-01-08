package fr.uha.ensisa.gl.testsquad.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;

@Controller
public class ExecutionDetailsController {

    @Autowired
    private DaoFactory dao;

    // For unit testing
    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    /**
     * Affiche les détails d'une exécution de test.
     */
    @GetMapping(value = "/execution")
    public ModelAndView viewExecutionDetails(@RequestParam(name = "id") long id) {
        ManualTestExecution execution = dao.getTestExecutionDao().find(id);

        if (execution == null || execution.getSteps().isEmpty()) {
            ModelAndView modelAndView = new ModelAndView("executionDetails");
            modelAndView.addObject("errorMessage", "Execution not found");
            return modelAndView;
        }

        ManualTest test = dao.getTestDao().find(execution.getTestId());

        ModelAndView modelAndView = new ModelAndView("executionDetails");
        modelAndView.addObject("execution", execution);
        modelAndView.addObject("test", test);
        modelAndView.addObject("id", id);
        modelAndView.addObject("status", execution.getStatus());
        modelAndView.addObject("steps", execution.getSteps());

        return modelAndView;
    }

}
