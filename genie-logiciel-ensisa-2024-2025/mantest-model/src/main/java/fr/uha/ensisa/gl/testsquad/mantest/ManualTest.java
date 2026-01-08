package fr.uha.ensisa.gl.testsquad.mantest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;
import fr.uha.ensisa.gl.testsquad.mantest.status.TestStatus;

public class ManualTest {

    protected long id;
    protected String name;
    protected String description;
    private final List<Step> steps;  // Gestion des étapes intégrée
    private boolean hasPassed = false; // Permet de distinguer une régression après un passage validé

    // Constructeurs
    public ManualTest() {
        this.steps = new ArrayList<>();
    }

    public ManualTest(long id) {
        this.id = id;
        this.steps = new ArrayList<>();
    }

    public ManualTest(long id, String name) {
        this.id = id;
        this.name = name;
        this.steps = new ArrayList<>();
    }

    public ManualTest(long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.steps = new ArrayList<>();
    }

    public ManualTest(long id, String name, String description, List<Step> steps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.steps = new ArrayList<>(steps);
    }

    // Getters et setters pour id, name et description
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Méthodes de gestion des étapes

    /**
     * Ajoute une étape en ne fournissant que sa description.
     * Le statut sera par défaut UNDEFINED.
     */
    public void addStep(String description) {
        this.steps.add(new Step(description));
    }

    /**
     * Ajoute une étape déjà construite.
     */
    public void addStep(Step step) {
        this.steps.add(step);
    }

    /**
     * Ajoute plusieurs étapes.
     */
    public void addSteps(List<Step> steps) {
        this.steps.addAll(steps);
    }

    /**
     * Supprime une étape donnée.
     */
    public void removeStep(Step step) {
        this.steps.remove(step);
    }

    /**
     * Supprime la première étape dont la description correspond.
     */
    public void removeStep(String description) {
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).getDescription().equals(description)) {
                steps.remove(i);
                break;
            }
        }
    }    

    /**
     * Supprime plusieurs étapes.
     */
    public void removeSteps(List<Step> steps) {
        this.steps.removeAll(steps);
    }

    /**
     * Supprime toutes les étapes.
     */
    public void removeAllSteps() {
        this.steps.clear();
    }

    /**
     * Renvoie une copie défensive de la liste des étapes pour préserver l'encapsulation.
     */
    public List<Step> getSteps() {
        return new ArrayList<>(this.steps);
    }

    /**
     * Calcule le statut du test en fonction des statuts des étapes.
     * <ul>
     *   <li>Si aucune étape n'est présente : NOT_STARTED</li>
     *   <li>Toutes les étapes ACCEPTED : PASSED</li>
     *   <li>Une ou plusieurs étapes IN_PROGRESS : IN_PROGRESS</li>
     *   <li>Une étape REFUSED : FAILED ou REGRESSION (si le test avait déjà passé auparavant)</li>
     * </ul>
     */
    public TestStatus getStatus() {
        if (steps.isEmpty()) {
            return TestStatus.NOT_STARTED;
        }

        boolean allPassed = true;
        boolean anyInProgress = false;
        boolean anyRefused = false;

        for (Step step : steps) {
            if (step.getStatus() == StepStatus.REFUSED) {
                anyRefused = true;
                allPassed = false;
                break;
            } else if (step.getStatus() == StepStatus.IN_PROGRESS) {
                anyInProgress = true;
                allPassed = false;
            } else if (step.getStatus() != StepStatus.ACCEPTED) {
                allPassed = false;
            }
        }

        if (allPassed) {
            hasPassed = true;
            return TestStatus.PASSED;
        } else if (anyRefused) {
            return hasPassed ? TestStatus.REGRESSION : TestStatus.FAILED;
        } else if (anyInProgress) {
            return TestStatus.IN_PROGRESS;
        } else {
            return TestStatus.NOT_STARTED;
        }
    }

    // Classe imbriquée Step
    public static class Step {
        private String description;
        private StepStatus status;

        public Step(String description) {
            this.description = description;
            this.status = StepStatus.UNDEFINED;
        }

        public Step(String description, StepStatus status) {
            this.description = description;
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public StepStatus getStatus() {
            return status;
        }

        public void setStatus(StepStatus status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "Step{" +
                    "description='" + description + '\'' +
                    ", status=" + status +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof Step))
                return false;
            Step step = (Step) o;
            return Objects.equals(description, step.description)
                    && status == step.status;
        }

        @Override
        public int hashCode() {
            return Objects.hash(description, status);
        }
    }
}
