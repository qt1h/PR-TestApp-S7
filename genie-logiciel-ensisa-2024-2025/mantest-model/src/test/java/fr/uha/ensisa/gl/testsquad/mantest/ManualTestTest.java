package fr.uha.ensisa.gl.testsquad.mantest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;
import fr.uha.ensisa.gl.testsquad.mantest.status.TestStatus;

public class ManualTestTest {
    ManualTest sut; // System Under ManualTest

    @BeforeEach
    void createTest() {
        sut = new ManualTest();
    }

    @Test
    @DisplayName("A test should have a name")
    void setName() {
        assertNull(sut.getName());
        String name = "A sample test name";
        sut.setName(name);
        assertEquals(name, sut.getName());
    }

    @Test
    @DisplayName("A test should have a description")
    void setDescription() {
        assertNull(sut.getDescription());
        String description = "A sample test description";
        sut.setDescription(description);
        assertEquals(description, sut.getDescription());
    }

    @Test
    @DisplayName("give and set id ")
    void setId() {
        assertEquals(0, sut.getId());
        long id = 1;
        sut.setId(id);
        assertEquals(id, sut.getId());
    }

    @Test
    @DisplayName("A test should have been created with id, name and description")
    void createTestWithIdNameDescription() {
        long id = 1;
        String name = "A sample test name";
        String description = "A sample test description";
        sut = new ManualTest(id, name, description);
        assertEquals(id, sut.getId());
        assertEquals(name, sut.getName());
        assertEquals(description, sut.getDescription());
    }

    @Test
    @DisplayName("A test should have been created with id and name")
    void createTestWithIdName() {
        long id = 1;
        String name = "A sample test name";
        sut = new ManualTest(id, name);
        assertEquals(id, sut.getId());
        assertEquals(name, sut.getName());
    }

    @Test
    @DisplayName("A test should have been created with id")
    void createTestWithId() {
        long id = 1;
        sut = new ManualTest(id);
        assertEquals(id, sut.getId());
    }

    @Test
    @DisplayName("should add a step using addStep and return it via getSteps")
    void testAddStep() {
        // Vérifier que la liste des étapes est vide au départ
        assertEquals(0, sut.getSteps().size());
        String stepDescription = "Step description";
        sut.addStep(stepDescription);

        List<ManualTest.Step> steps = sut.getSteps();
        assertEquals(1, steps.size());
        ManualTest.Step step = steps.get(0);
        assertEquals(stepDescription, step.getDescription());
        // Le statut par défaut doit être UNDEFINED
        assertEquals(StepStatus.UNDEFINED, step.getStatus());
    }

    @Test
    @DisplayName("getSteps should return a defensive copy of the internal steps list")
    void testGetStepsDefensiveCopy() {
        sut.addStep("Step 1");
        // Récupération d'une copie de la liste des étapes
        List<ManualTest.Step> stepsCopy = sut.getSteps();
        // Modification de la copie ne doit pas impacter la liste interne
        stepsCopy.clear();
        List<ManualTest.Step> stepsAfterClear = sut.getSteps();
        assertEquals(1, stepsAfterClear.size());
    }

    @Test
    @DisplayName("should add multiple steps using addSteps(List<Step>)")
    void testAddSteps() {
        // Au départ, la liste est vide
        assertEquals(0, sut.getSteps().size());

        List<Step> stepsToAdd = new ArrayList<>();
        stepsToAdd.add(new Step("Step 1", StepStatus.UNDEFINED));
        stepsToAdd.add(new Step("Step 2", StepStatus.ACCEPTED));

        sut.addSteps(stepsToAdd);

        List<Step> steps = sut.getSteps();
        assertEquals(2, steps.size());
        assertEquals("Step 1", steps.get(0).getDescription());
        assertEquals("Step 2", steps.get(1).getDescription());
    }

    @Test
    @DisplayName("should remove a step by object using removeStep(Step)")
    void testRemoveStepByObject() {
        Step step = new Step("Step to remove", StepStatus.IN_PROGRESS);
        sut.addStep(step);
        // Vérifier l'ajout
        assertEquals(1, sut.getSteps().size());

        sut.removeStep(step);
        assertEquals(0, sut.getSteps().size());
    }

    @Test
    @DisplayName("should remove only the first step matching the given description using removeStep(String)")
    void testRemoveStepByDescription() {
        // Ajout de deux étapes avec la même description
        sut.addStep("Duplicate");
        sut.addStep("Duplicate");
        assertEquals(2, sut.getSteps().size());

        // Supprime uniquement la première occurrence
        sut.removeStep("Duplicate");

        // Après suppression, il doit rester une seule étape avec la description
        // "Duplicate"
        List<ManualTest.Step> remaining = sut.getSteps();
        assertEquals(1, remaining.size());
        assertEquals("Duplicate", remaining.get(0).getDescription());
    }

    @Test
    @DisplayName("removeStep(String) with non-matching description does nothing and does not throw exception")
    void testRemoveStepNonExisting() {
        // Ajoute quelques étapes
        sut.addStep("Step A");
        sut.addStep("Step B");
        int originalSize = sut.getSteps().size();

        // Appel avec une description qui ne correspond à aucune étape
        sut.removeStep("NonExisting");

        // Vérifie qu'aucune étape n'a été supprimée et qu'aucune exception n'a été
        // levée
        assertEquals(originalSize, sut.getSteps().size());
    }

    @Test
    @DisplayName("should remove multiple steps using removeSteps(List<Step>)")
    void testRemoveMultipleSteps() {
        Step step1 = new Step("Step 1", StepStatus.UNDEFINED);
        Step step2 = new Step("Step 2", StepStatus.ACCEPTED);
        Step step3 = new Step("Step 3", StepStatus.IN_PROGRESS);
        sut.addStep(step1);
        sut.addStep(step2);
        sut.addStep(step3);
        assertEquals(3, sut.getSteps().size());

        List<Step> stepsToRemove = new ArrayList<>();
        stepsToRemove.add(step1);
        stepsToRemove.add(step3);

        sut.removeSteps(stepsToRemove);
        List<Step> remaining = sut.getSteps();
        assertEquals(1, remaining.size());
        assertEquals("Step 2", remaining.get(0).getDescription());
    }

    @Test
    @DisplayName("should remove all steps using removeAllSteps()")
    void testRemoveAllSteps() {
        sut.addStep("Step 1");
        sut.addStep("Step 2");
        sut.addStep("Step 3");
        assertEquals(3, sut.getSteps().size());

        sut.removeAllSteps();
        assertEquals(0, sut.getSteps().size());
    }

    @Test
    @DisplayName("getStatus returns FAILED when a step is refused and hasPassed is false")
    void testGetStatusRefusedWithoutPreviousPass() {
        ManualTest test = new ManualTest(1, "Test", "Description");
        // Ajout d'une étape refusée
        ManualTest.Step refusedStep = new ManualTest.Step("Step 1", StepStatus.REFUSED);
        test.addStep(refusedStep);
        // hasPassed est false par défaut
        assertEquals(TestStatus.FAILED, test.getStatus(), 
            "Si aucune étape n'a validé le test, une étape refusée doit renvoyer FAILED");
    }

    @Test
    @DisplayName("getStatus returns REGRESSION when a step is refused and hasPassed is true")
    void testGetStatusRefusedAfterPreviousPass() {
        ManualTest test = new ManualTest(1, "Test", "Description");
        // Ajout d'une étape acceptée pour marquer le test comme passé
        ManualTest.Step acceptedStep = new ManualTest.Step("Step Accepted", StepStatus.ACCEPTED);
        test.addStep(acceptedStep);
        // Appel pour que hasPassed soit mis à true (car toutes les étapes sont ACCEPTED)
        assertEquals(TestStatus.PASSED, test.getStatus(), 
            "Si toutes les étapes sont acceptées, le test doit être PASSED et hasPassed mis à true");
        
        // Ajout d'une étape refusée après le passage validé
        ManualTest.Step refusedStep = new ManualTest.Step("Step Refused", StepStatus.REFUSED);
        test.addStep(refusedStep);
        // Maintenant, getStatus() doit retourner REGRESSION car hasPassed est true
        assertEquals(TestStatus.REGRESSION, test.getStatus(), 
            "Si le test a déjà été validé, une étape refusée doit renvoyer REGRESSION");
    }

}
