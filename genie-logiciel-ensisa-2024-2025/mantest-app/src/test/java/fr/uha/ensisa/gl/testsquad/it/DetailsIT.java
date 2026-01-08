package fr.uha.ensisa.gl.testsquad.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

public class DetailsIT extends BaseIT {

    @Test
    public void testDetailsPageLoad() throws IOException {
        int testId = 1;
        int statusCode = getHTTPStatusCode(getBaseUrl() + "test?id=" + testId);
        assertEquals(200, statusCode, "Expected HTTP status code 200");
    }

    @Test
    public void testDetails() {
        int id = 2;
        String expectedName = "Test" + id;
        String expectedDescription = "Test" + id + "_description";
        String[] expectedSteps = {"Step_1", "Step_2"};
        
        // Naviguer vers la page de détails et vérifier les informations
        navigateTo("test?id=" + id);
        verifyTestDetails(expectedName, expectedDescription, 10, expectedSteps);
    }

    @Test
    public void testRemoveTest() {
        int id = 1;
        // Supprimer le test et vérifier qu'il n'existe plus
        removeTestAndVerify(id, 10);
    }

    @Test
    public void testEditTestWithAddAndDeleteStep() {
        int id = 2;
        String originalName = "Test" + id;
        String originalDescription = "Test" + id + "_description";
        // Optionnel : vérifier les valeurs initiales
        navigateTo("test?id=" + id);
        verifyTestDetails(originalName, originalDescription, 10, new String[] {"Step_1", "Step_2"});

        // Modifier le test : mettre à jour le nom, la description et ajouter une nouvelle étape
        String newName = "Updated_Test";
        String newDescription = "Updated_Test_Description";
        String newStepToAdd = "Updated_Step_2";
        editTest(newName, newDescription, newStepToAdd, 10);

        // Vérifier que la page de détails affiche les nouvelles valeurs
        String[] expectedSteps = {"Step_2", newStepToAdd};
        navigateTo("test?id=" + id);
        verifyTestDetails(newName, newDescription, 10, expectedSteps);

        // Vérifier le nombre total d'étapes
        int actualStepsCount = driver.findElements(By.xpath("//ul[@id='steps-list']/li")).size();
        int expectedStepsCount = 2; // 1 étape initiale conservée + 1 nouvelle étape ajoutée
        assertEquals(expectedStepsCount, actualStepsCount, "The number of steps after editing is incorrect.");
    }
}

    /*
    @Test
    public void testValidationDateDisplayedForAcceptedStep() {
        int id = 3;
        driver.get(getBaseUrl() + "test?id=" + id);
        
        // 1. Attendre que le premier menu déroulant des états soit visible
        WebElement firstStepStatusDropdown = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ul[@id='steps-list']//select[@name='stepStatuses']")));
        
        // 2. Modifier sa valeur pour passer à "ACCEPTED"
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(firstStepStatusDropdown);
        select.selectByValue("ACCEPTED");
        
        // 3. Cliquer sur le bouton Save (en scrollant et en utilisant JavaScript pour éviter les problèmes d'interception)
        WebElement saveButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("save-button")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", saveButton);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saveButton);
        
        // 4. Attendre que l'URL contienne "test?id=3" (indiquant que la page a été rechargée)
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("test?id=" + id));
        
        // 5. Re-localiser le menu déroulant dont l'option sélectionnée a la valeur "ACCEPTED"
        WebElement acceptedDropdown = new WebDriverWait(driver, Duration.ofSeconds(10))
          .until(driver -> {
              List<WebElement> dropdowns = driver.findElements(By.xpath("//ul[@id='steps-list']//select[@name='stepStatuses']"));
              for (WebElement dd : dropdowns) {
                  try {
                      org.openqa.selenium.support.ui.Select s = new org.openqa.selenium.support.ui.Select(dd);
                      if ("ACCEPTED".equals(s.getFirstSelectedOption().getAttribute("value"))) {
                          return dd;
                      }
                  } catch (org.openqa.selenium.StaleElementReferenceException e) {
                      // Si l'élément est devenu périmé, on continue la boucle pour le re-localiser.
                  }
              }
              return null;
          });
        assertNotNull(acceptedDropdown, "No step with ACCEPTED status was found after update.");
        
        // 6. À partir du menu déroulant ACCEPTED, trouver la div parente (la zone "step-extra") qui contient la date
        WebElement validationDateSpan = driver.findElement(By.xpath(".//span[@class='step-validation-date-view']"));
        String dateText = validationDateSpan.getText();
        
        // 7. Vérifier que la date affichée n'est pas "N/A" et n'est pas vide
        assertTrue(!"N/A".equals(dateText) && !dateText.isEmpty(),
             "Validation date should be displayed for an accepted step.");
    }

     */
    

    

