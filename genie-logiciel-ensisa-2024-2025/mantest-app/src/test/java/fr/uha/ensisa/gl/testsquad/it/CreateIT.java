package fr.uha.ensisa.gl.testsquad.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class CreateIT extends BaseIT {

    @Test
    public void testCreatePageLoad() throws IOException {
        int statusCode = getHTTPStatusCode(getBaseUrl() + "testCreate");
        assertEquals(200, statusCode, "Expected HTTP status code 200");
    }

    @Test
    public void testNavigateToCreatePage() {
        navigateTo("testCreate");
        assertEquals(getBaseUrl() + "testCreate", driver.getCurrentUrl(), "Failed to navigate to the 'create' page.");
    }

    @Test
    public void testFormElementsExistence() {
        navigateTo("testCreate");
        waitForElements(10,
                By.id("testName"),
                By.id("testSteps_input"),
                By.id("description"),
                By.xpath("//button[@type='submit']"),
                By.id("addIt"));

    }

    @Test
    public void testAddItemsToList() {
        List<String> items = Arrays.asList("step 1", "step 2");
        navigateTo("testCreate");
        for (String item : items) {
            addItemToList(item, 10);
        }
    }

    @Test
    public void testFormSubmission() throws IOException {
        String testName = "Test1";
        String testStep = "Step_1";
        String testDescription = "Test1_description";

        navigateTo("testCreate");

        // Remplit le formulaire, gère l'alerte et soumet à nouveau
        submitFormAndHandleAlert(testName, testDescription, testStep, 10);

        // Vérifie que la navigation vers la liste s'est effectuée
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("testList"), "Failed to navigate to the 'list' page.");

        // Valider la présence du test dans la liste et vérifier la page de détails
        validateTestListingAndDetails(testName, testDescription, testStep, 1, 10);
    }
}
