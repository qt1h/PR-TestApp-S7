package fr.uha.ensisa.gl.testsquad.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;

@Controller
public class TestDetailsController {

    @Autowired
    private DaoFactory dao;

    // For unit testing
    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping(value = "/test")
    public ModelAndView viewTestDetails(@RequestParam(name = "id") long id) {
        ManualTest test = dao.getTestDao().find(id);
        // If test exists but has no steps, treat it as "not found"
        if (test != null && test.getSteps().isEmpty()) {
            test = null;
        }
        ModelAndView modelAndView = new ModelAndView("testDetails");
        if (test != null) {

            modelAndView.addObject("test", test);
            modelAndView.addObject("id", id);
            modelAndView.addObject("steps", test.getSteps());
            modelAndView.addObject("testStatus", test.getStatus());
        } else {
            modelAndView.addObject("errorMessage", "Test not found");
        }
        return modelAndView;
    }

    @PostMapping(value = "/removeTest")
    public ResponseEntity<String> deleteTest(@RequestParam(name = "id") long id) {
        TestDao testDao = dao.getTestDao();
        ManualTest test = testDao.find(id);
        if (test != null) {
            testDao.remove(id);
            dao.getTestExecutionDao().removeByTestId(id);

            // Parcourir toutes les suites pour retirer le test supprimé
            dao.getSuiteDao().findAll().forEach(suite -> {
                if (suite.getTests().stream().anyMatch(t -> t.getId() == id)) {
                    suite.removeTest(test);
                    dao.getSuiteDao().update(suite);
                }
            });
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./testList").build();
        } else {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./404").build();
        }
    }

    /**
     * Met à jour le test en utilisant le nom, la description, la liste des étapes
     * et la liste des états pour chaque étape. Si l'état est ACCEPTED,
     * la date de validation est automatiquement définie.
     */
    @PostMapping(value = "/updateTest")
    public ResponseEntity<String> updateTest(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "name") String name,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "steps", required = false) List<String> steps) {

        TestDao testDao = dao.getTestDao();

        ManualTest test = testDao.find(id);
        if (test == null) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./404").build();
        }

        // Validation du nom et de la description
        if (name == null || description == null || name.strip().isEmpty()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        // Validation des listes d'étapes et de statuts
        if (steps == null || steps.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        // Construction de la liste des nouvelles étapes
        List<Step> updatedSteps = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            String stepStr = steps.get(i);
            if (stepStr == null || stepStr.strip().isEmpty()) {
                return ResponseEntity.status(HttpStatus.SEE_OTHER)
                        .header("Location", "./400").build();
            }
            String stepDesc = stepStr.strip();

            Step newStep = new Step(stepDesc);
            updatedSteps.add(newStep);
        }

        // Mise à jour du test
        test.setName(name.strip());
        test.setDescription(description.strip());
        test.removeAllSteps();
        // Ajout de toutes les étapes en une seule opération
        test.addSteps(updatedSteps);

        testDao.update(test);

        return ResponseEntity.status(HttpStatus.SEE_OTHER).header("Location", "./test?id=" + id).build();
    }
}
