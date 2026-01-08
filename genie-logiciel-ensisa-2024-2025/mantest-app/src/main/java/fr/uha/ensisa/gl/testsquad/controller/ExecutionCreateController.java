package fr.uha.ensisa.gl.testsquad.controller;

import java.time.LocalDateTime;
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
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution.StepExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;

@Controller
public class ExecutionCreateController {

    @Autowired
    private DaoFactory dao;

    // For unit testing
    void setDao(DaoFactory dao) {
        this.dao = dao;
    }

    @GetMapping(value = "/executionCreate")
    public ModelAndView viewExecutionCreate(@RequestParam(name = "id") long id) {
        ManualTest test = dao.getTestDao().find(id);
        // If test exists but has no steps, treat it as "not found"
        if (test != null && test.getSteps().isEmpty()) {
            test = null;
        }
        ModelAndView modelAndView = new ModelAndView("executionCreate");
        if (test != null) {

            modelAndView.addObject("id", id);
            modelAndView.addObject("test", test);
            modelAndView.addObject("steps", test.getSteps());
            modelAndView.addObject("stepsCount", test.getSteps().size());

        } else {
            modelAndView.addObject("errorMessage", "Test not found");
        }
        return modelAndView;
    }

    @PostMapping(value = "/executionCreate")
    public ResponseEntity<String> viewExecutionCreate(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "executionComment", required = true) String executionComment,
            @RequestParam(name = "stepsStatuses", required = true) List<String> stepsStatuses,
            @RequestParam(name = "stepsComments", required = true) List<String> stepsComments) {

        TestExecutionDao executionDao = dao.getTestExecutionDao();
        TestDao testDao = dao.getTestDao();

        ManualTest test = testDao.find(id);

        if (test == null) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./404").build();
        }

        // Vérification du commentaire global : il doit être non-null (peut etre vide car optionnel)
        if (executionComment == null) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        // Vérifier que la liste des status n'est pas nulle
        if (stepsStatuses == null || stepsStatuses.stream().anyMatch(c -> c == null || c.strip().isEmpty())) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        // pour le cas ou le test n'a qu'une étape et le commentaire est vide
        if (stepsStatuses.size() == 1 && stepsComments.isEmpty()) {
            stepsComments = List.of("");
        }

        // Vérifier que la liste des commentaires d'étape n'est pas nulle
        // et qu'aucun commentaire est null
        if (stepsComments == null || stepsComments.stream().anyMatch(c -> c == null)) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        // Vérifier que la taille est la meme
        if (stepsComments.size() != stepsStatuses.size()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        // Récupération des étapes du test
        List<Step> steps = test.getSteps();
        if (steps == null || steps.isEmpty() || stepsComments.size() != steps.size()) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header("Location", "./400").build();
        }

        // Pour chaque étape, mettre à jour son status
        List<Step> updatedSteps = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {

            String stepDesc = steps.get(i).getDescription();
            String statusStr = stepsStatuses.get(i);

            StepStatus status = parseStepStatus(statusStr);
            if (status == null) {
                return ResponseEntity.status(HttpStatus.SEE_OTHER).header("Location", "./400").build();
            } else {
                Step newStep = new Step(stepDesc);
                newStep.setStatus(status);
                updatedSteps.add(newStep);
            }
        }

        //sauvegarde du nouveau statu du test
        test.removeAllSteps();
        test.addSteps(updatedSteps);
        testDao.update(test);

        //création de l'éxecution meme
        ManualTestExecution execution = new ManualTestExecution(0, test, LocalDateTime.now());

        // Mise à jour du commentaire global
        execution.setComment(executionComment.strip());

        // Mise à jour du commentaire local
        for (int i = 0; i < steps.size(); i++) {
            StepExecution stepExec = execution.getSteps().get(i);
            stepExec.setComment(stepsComments.get(i).strip());
        }

        executionDao.persist(execution);

        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .header("Location", "./testReport?id=" + id).build();
    }

    private StepStatus parseStepStatus(String statusStr) {

        for (StepStatus s : StepStatus.values()) {
            if (s.name().equals(statusStr)) {
                return s;
            }
        }
        return null;
    }
}
