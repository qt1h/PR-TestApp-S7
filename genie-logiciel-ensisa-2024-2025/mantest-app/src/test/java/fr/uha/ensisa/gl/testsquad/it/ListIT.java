package fr.uha.ensisa.gl.testsquad.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ListIT extends BaseIT {

    @Test
    public void testListPageLoad() throws IOException {
        int statusCode = getHTTPStatusCode(getBaseUrl() + "testList");
        assertEquals(200, statusCode, "Expected HTTP status code 200");
    }

    @Test
    public void testList() {
        int id = 1;
        String expectedName = "Test" + id;
        String expectedDescription = "Test" + id + "_description";
        
        navigateTo("testList");
        verifyTestListEntry(id, expectedName, expectedDescription, 10);
    }

    @Test
    public void testClickOnTest() {
        int id = 1;
        navigateTo("testList");
        String elementId = "test_" + id;
        
        // Récupération de l'élément cliquable (le nom du test)
        WebElement link = waitForElement(By.cssSelector("#" + elementId + " .name"), 10);
        link.click();
        waitForUrlContains("/test?id=" + id, 10);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/test?id=" + id), "Navigation should succeed");
    }
}
