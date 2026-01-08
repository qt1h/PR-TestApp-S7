package fr.uha.ensisa.gl.testsquad.mantest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;
import fr.uha.ensisa.gl.testsquad.mantest.status.TestStatus;

public class ManualTestExecution {

    private long id;
    private final long testId;
    private String comment;
    private LocalDateTime date;
    private final List<StepExecution> steps;
    private final TestStatus status;

    public ManualTestExecution(long id, ManualTest test) {
        this(id, test, null);
    }

    public ManualTestExecution(long id, ManualTest test, LocalDateTime date) {
        this.id = id;
        this.testId = test.getId();
        this.comment = "";
        this.date = date;
        this.steps = new ArrayList<>();
        this.status = test.getStatus();
        for (Step step : test.getSteps()) {
            this.steps.add(new StepExecution(step));
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTestId() {
        return testId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Retourne une copie défensive de la liste des StepExecution.
     */
    public List<StepExecution> getSteps() {
        return new ArrayList<>(steps);
    }

    public TestStatus getStatus() {
        return status;
    }

    /**
     * Classe imbriquée qui représente l'exécution d'une étape.
     */
    public static class StepExecution {

        private final Step step;
        private String comment;
        private final StepStatus status;

        public StepExecution(Step step) {
            // Crée une copie de l'étape pour préserver l'encapsulation
            this.step = new Step(step.getDescription(), step.getStatus());
            this.status = step.getStatus();
            this.comment = "";
        }

        public StepExecution(Step step, StepStatus status) {
            this.step = new Step(step.getDescription(), step.getStatus());
            this.status = status;
            this.comment = "";
        }

        /**
         * Retourne une copie de l'étape exécutée.
         */
        public Step getStep() {
            return new Step(step.getDescription(), step.getStatus());
        }

        public String getDescription() {
            return step.getDescription();
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public StepStatus getStatus() {
            return status;
        }
    }
}
