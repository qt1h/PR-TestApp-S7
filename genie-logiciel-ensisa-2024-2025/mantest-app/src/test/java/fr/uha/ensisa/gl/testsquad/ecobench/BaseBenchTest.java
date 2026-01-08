package fr.uha.ensisa.gl.testsquad.ecobench;

import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;
import fr.uha.ensisa.eco.metrologie.extension.EcoExtension;
import fr.uha.ensisa.eco.metrologie.extension.annotations.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@EcoDocker(network = "mantest-app-metrologie", clean = true)
@EcoDockerContainer(id = "mantest-app-proxy-1", port = 8081)
@EcoMonitor(containerId = "mantest-app-tomcat-1")
@EcoWebDriver(remote = true) // <- change this to use local browser
@EcoEnergyCounter(type = EcoEnergyCounterType.POWERSPY, name = "Main", endPoint = "$POWERSPY_HOST$")
@EcoGatling(userCount = 25, rampDuration = 10)

@ExtendWith(EcoExtension.class)
public abstract class BaseBenchTest {

    public void fillField(By locator, String text, long timeoutSeconds, WebDriver wb) {
        WebElement field = waitForElement(locator, timeoutSeconds, wb);
        field.clear();
        field.sendKeys(text);
    }

    public Alert waitForAlert(long timeoutSeconds, WebDriver wb) {
        return getWait(timeoutSeconds, wb).until(ExpectedConditions.alertIsPresent());
    }

    public WebElement waitForElement(By locator, long timeoutSeconds, WebDriver wb) {
        return getWait(timeoutSeconds, wb).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void scrollClick(WebElement element, WebDriver wb) {
        // Scroll to the element (to bring it to the view and avoid issues with elements
        // not visible)
        ((JavascriptExecutor) wb).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();
    }

    public void addItemToList(String item, long timeoutSeconds, WebDriver wb) {
        WebElement listItemElt = waitForElement(By.id("testSteps_input"), timeoutSeconds, wb);
        WebElement addItemButton = waitForElement(By.id("addIt"), timeoutSeconds, wb);
        listItemElt.clear();
        listItemElt.sendKeys(item);
        scrollClick(addItemButton, wb);
        getWait(timeoutSeconds, wb)
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//ul[@id='dynamic-list']/li/span[contains(text(), '" + item + "')]")));
    }

    public void waitForUrlContains(String fragment, long timeoutSeconds, WebDriver wb) {
        getWait(timeoutSeconds, wb).until(ExpectedConditions.urlContains(fragment));
    }

    private WebDriverWait getWait(long timeoutSeconds, WebDriver wb) {
        return new WebDriverWait(wb, Duration.ofSeconds(timeoutSeconds));
    }

    public void submitFormAndHandleAlert(String testName, String testDescription, String testStep, long timeoutSeconds, WebDriver wb) {
        // Remplir les champs du formulaire
        fillField(By.id("testName"), testName, timeoutSeconds, wb);
        fillField(By.id("description"), testDescription, timeoutSeconds, wb);

        // Première soumission sans étape pour déclencher l'alerte
        WebElement submitButton = waitForElement(By.xpath("//button[@type='submit']"), timeoutSeconds, wb);
        scrollClick(submitButton, wb);

        String expectedAlertMessage = "Please add at least one step before submitting the form.";
        Alert alert = waitForAlert(timeoutSeconds, wb);
        assertNotNull(alert, "Expected an alert dialog to appear.");
        assertEquals(expectedAlertMessage, alert.getText(), "Alert message did not match the expected text.");
        alert.dismiss();

        // Ajout d'une étape et nouvelle soumission
        addItemToList(testStep, timeoutSeconds, wb);
        submitButton = waitForElement(By.xpath("//button[@type='submit']"), timeoutSeconds, wb);
        submitButton.click();
        waitForUrlContains("testList", timeoutSeconds , wb);
    }

}
