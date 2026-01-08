package fr.uha.ensisa.gl.testsquad.it;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseIT {
    public static WebDriver driver;
    protected static String host, port;

    @BeforeAll
    public static void setupWebDriver() {
        if (driver != null)
            return;

        host = System.getProperty("host", "172.17.0.1");
        port = System.getProperty("servlet.port", "8080");

        // Set Firefox options
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless"); // Runs without opening UI
        options.setCapability("webSocketUrl", true);

        boolean skipRemote = Boolean.parseBoolean(System.getProperty("skip.remoteIT", "true"));
        String remoteUrl = System.getProperty("webdriver.url", "http://localhost:4444/wd/hub");

        if (!skipRemote) {
            // Try RemoteWebDriver first
            try {
                driver = new RemoteWebDriver(URI.create(remoteUrl).toURL(), options);
                return;
            } catch (Exception e) {
                System.err.println("Failed to connect to remote WebDriver, falling back to local execution.");
            }
        }

        host = "localhost";

        // Looking for marionette in PATH
        String ext = System.getProperty("os.name", "")
                .toLowerCase().startsWith("win") ? ".exe" : "";
        String geckodrivername = "geckodriver" + ext;
        Collection<String> pathes = new ArrayList<>();
        for (String source : new String[] {
                System.getProperty("PATH") /* posix */,
                System.getenv().get("Path") /* win < 10 */,
                System.getenv().get("PATH") /* win >= 10 */ }) {
            if (source != null)
                pathes.addAll(Arrays.asList(source.trim().split(File.pathSeparator)));
        }
        File geckoDriver = null;
        for (String path : pathes) {
            File f = new File(path, geckodrivername);
            if (f.exists() && f.canExecute()) {
                System.setProperty("webdriver.gecko.driver", f.getAbsolutePath());
                geckoDriver = f;
                break;
            }
        }
        if (geckoDriver == null)
            throw new IllegalStateException("Cannot find geckodriver on " + pathes);

        // System.out.println("Using gecko driver " + geckoDriver.getAbsolutePath());

        // Initialize WebDriver
        driver = new FirefoxDriver(options);
    }

    @BeforeEach
    public void resetDataBeforeTest() throws IOException {
        String baseUrl = getBaseUrl() + "testCreate";
        String payload;
        // liste d'étapes sous forme de JSON
        String stepsJson = URLEncoder.encode("[\"Step_1\",\"Step_2\"]", StandardCharsets.UTF_8);

        for (int i = 1; i < 4; i++) {
            payload = "testName=Test" + i
                    + "&testStepsJson=" + stepsJson
                    + "&description=Test" + i + "_description";
            sendPostRequest(baseUrl, payload);
        }
    }

    @AfterEach
    public void resetDataAfterTest() throws IOException {

        driver.get(getBaseUrl() + "admin");
        WebElement resetButton = driver.findElement(By.id("reset-button"));
        scrollClick(resetButton);

    }

    @AfterAll
    public static void shutdownWebDriver() {
        if (driver != null) {
            driver.quit();
            /*
             * try {
             * driver.close();
             * } catch (Exception x) {
             * throw new RuntimeException(x);
             * }
             */
            driver = null;
        }
    }

    public static String getBaseUrl() {
        return "http://" +
                host + ":" +
                port + '/';
    }

    public void sendPostRequest(String urlString, String payload) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes());
            os.flush();
        }

        if (connection.getResponseCode() != 200) {
            throw new IllegalStateException("Unexpected response code: " + connection.getResponseCode());
        }
    }

    public void scrollClick(WebElement element) {
        // Scroll to the element (to bring it to the view and avoid issues with elements
        // not visible)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();
    }

    public void waitForTitle(String titleFragment, long timeoutSeconds) {
        getWait(timeoutSeconds)
                .until(ExpectedConditions.titleContains(titleFragment));
    }

    public void navigateAndAssertTitle(String path, String expectedTitleFragment, long timeoutSeconds) {
        driver.get(getBaseUrl() + path);
        waitForTitle(expectedTitleFragment, timeoutSeconds);
        String pageTitle = driver.getTitle();
        assertNotNull(pageTitle, "Le titre de la page ne devrait pas être nul");
        assertTrue(pageTitle.contains(expectedTitleFragment),
                "La page affichée devrait contenir \"" + expectedTitleFragment + "\" dans le titre");
    }

    // Retourne le code HTTP de la réponse pour une URL donnée
    public int getHTTPStatusCode(String urlString) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");
        return connection.getResponseCode();
    }

    public WebDriverWait getWait(long timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    // Clique sur un lien identifié par son id et vérifie que l'URL courante
    // contient le chemin attendu
    public void clickLinkAndAssertNavigation(String linkId, String expectedPath, long timeoutSeconds) {
        WebElement link = driver.findElement(By.id(linkId));
        link.click();
        getWait(timeoutSeconds).until(d -> Objects.requireNonNull(d.getCurrentUrl()).contains(expectedPath));
        String currentUrl = driver.getCurrentUrl();
        assert currentUrl != null;
        assertTrue(currentUrl.contains(expectedPath), "Navigation vers " + expectedPath + " devrait réussir");
    }

    // Navigue vers une page en utilisant le chemin relatif
    public void navigateTo(String path) {
        driver.get(getBaseUrl() + path);
    }

    // Attend qu'un élément soit présent et le retourne
    public WebElement waitForElement(By locator, long timeoutSeconds) {
        return getWait(timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void waitForElements(long timeoutSeconds, By... locators) {
        WebDriverWait wait = getWait(timeoutSeconds);
        for (By locator : locators) {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        }
    }

    // Ajoute un élément dans la liste sur la page "create"
    public void addItemToList(String item, long timeoutSeconds) {
        WebElement listItemElt = waitForElement(By.id("testSteps_input"), timeoutSeconds);
        WebElement addItemButton = waitForElement(By.id("addIt"), timeoutSeconds);
        listItemElt.clear();
        listItemElt.sendKeys(item);
        scrollClick(addItemButton);
        getWait(timeoutSeconds)
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//ul[@id='dynamic-list']/li/span[contains(text(), '" + item + "')]")));
    }

    // Remplit un champ en le vidant d'abord
    public void fillField(By locator, String text, long timeoutSeconds) {
        WebElement field = waitForElement(locator, timeoutSeconds);
        field.clear();
        field.sendKeys(text);
    }

    // Attend et retourne une alerte
    public Alert waitForAlert(long timeoutSeconds) {
        return getWait(timeoutSeconds)
                .until(ExpectedConditions.alertIsPresent());
    }

    // Attend que l'URL contienne une chaîne donnée
    public void waitForUrlContains(String fragment, long timeoutSeconds) {
        getWait(timeoutSeconds)
                .until(ExpectedConditions.urlContains(fragment));
    }

    // Remplit le formulaire, soumet une première fois pour déclencher l'alerte,
    // ajoute une étape, puis re-soumet
    public void submitFormAndHandleAlert(String testName, String testDescription, String testStep,
            long timeoutSeconds) {
        // Remplir les champs du formulaire
        fillField(By.id("testName"), testName, timeoutSeconds);
        fillField(By.id("description"), testDescription, timeoutSeconds);

        // Première soumission sans étape pour déclencher l'alerte
        WebElement submitButton = waitForElement(By.xpath("//button[@type='submit']"), timeoutSeconds);
        scrollClick(submitButton);

        String expectedAlertMessage = "Please add at least one step before submitting the form.";
        Alert alert = waitForAlert(timeoutSeconds);
        assertNotNull(alert, "Expected an alert dialog to appear.");
        assertEquals(expectedAlertMessage, alert.getText(), "Alert message did not match the expected text.");
        alert.dismiss();

        // Ajout d'une étape et nouvelle soumission
        addItemToList(testStep, timeoutSeconds);
        submitButton = waitForElement(By.xpath("//button[@type='submit']"), timeoutSeconds);
        submitButton.click();
        waitForUrlContains("testList", timeoutSeconds);
    }

    // Valide que le test est listé et vérifie ensuite la page de détails
    public void validateTestListingAndDetails(String testName, String testDescription, String testStep, int testId,
            long timeoutSeconds) {
        // Vérifier la liste
        WebElement testNameInList = waitForElement(By.cssSelector("#test_" + testId + " .name"), timeoutSeconds);
        WebElement testDescriptionInList = waitForElement(By.cssSelector("#test_" + testId + " .description"),
                timeoutSeconds);
        assertEquals(testName, testNameInList.getText(), "Test name in the list does not match.");
        assertEquals(testDescription, testDescriptionInList.getText(), "Test description in the list does not match.");

        // Cliquer pour voir les détails
        testNameInList.click();

        // Vérifier la page de détails
        WebElement nameView = waitForElement(By.cssSelector(".name-view"), timeoutSeconds);
        WebElement descriptionView = waitForElement(By.cssSelector(".description-view"), timeoutSeconds);
        WebElement stepView = waitForElement(
                By.xpath("//ul[@id='steps-list']/li//span[contains(@class, 'step-view') and contains(text(), '"
                        + testStep + "')]"),
                timeoutSeconds);

        assertEquals(testName, nameView.getText(), "Test name on the details page does not match.");
        assertEquals(testDescription, descriptionView.getText(),
                "Test description on the details page does not match.");
        assertEquals(testStep, stepView.getText(), "Test step on the details page does not match.");
    }

    // Vérifie que la page de détails affiche les valeurs attendues pour le nom, la
    // description et les étapes.
    // Le paramètre timeoutSeconds est utilisé pour toutes les attentes.
    public void verifyTestDetails(String expectedName, String expectedDescription, long timeoutSeconds,
            String... expectedSteps) {
        WebElement nameElt = waitForElement(By.cssSelector(".name-view"), timeoutSeconds);
        assertEquals(expectedName, nameElt.getText(), "Le nom du test est incorrect.");

        WebElement descriptionElt = waitForElement(By.cssSelector(".description-view"), timeoutSeconds);
        assertEquals(expectedDescription, descriptionElt.getText(), "La description du test est incorrecte.");

        for (int i = 0; i < expectedSteps.length; i++) {
            WebElement stepElt = waitForElement(
                    By.xpath("//ul[@id='steps-list']/li[" + (i + 1) + "]//span[contains(@class, 'step-view')]"),
                    timeoutSeconds);
            assertEquals(expectedSteps[i], stepElt.getText(), "L'étape " + (i + 1) + " est incorrecte.");
        }
    }

    // Supprime le test identifié par son ID et vérifie qu'il n'est plus accessible.
    public void removeTestAndVerify(int testId, long timeoutSeconds) {
        // Charger la page de détails pour vérifier l'existence
        navigateTo("test?id=" + testId);
        WebElement nameElt = waitForElement(By.cssSelector(".name-view"), timeoutSeconds);
        String testName = nameElt.getText();
        assertNotNull(testName, "Le test avec l'ID " + testId + " devrait exister avant suppression.");

        // Soumettre la suppression
        WebElement deleteButton = waitForElement(By.cssSelector("form[action='/removeTest'] button"), timeoutSeconds);
        scrollClick(deleteButton);

        // Tenter d'accepter une alerte de confirmation, si présente
        try {
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            System.out.println("Aucune alerte détectée lors de la suppression.");
        }

        // Attendre la redirection vers la liste des tests
        waitForUrlContains("/testList", timeoutSeconds);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains("/testList"),
                "L'utilisateur devrait être redirigé vers la liste des tests après suppression.");

        // Vérifier que le test n'existe plus en rechargeant la page de détails
        navigateTo("test?id=" + testId);
        String pageSource = driver.getPageSource();
        assertNotNull(pageSource, "La page des détails devrait être accessible.");
        assertTrue(pageSource.contains("Test not found") || pageSource.contains("errorMessage"),
                "La page des détails devrait indiquer que le test est introuvable après suppression.");
    }

    // Effectue l'édition d'un test : clique sur le bouton d'édition, modifie le nom
    // et la description,
    // ajoute une nouvelle étape, puis sauvegarde les modifications.
    public void editTest(String newName, String newDescription, String newStepToAdd, long timeoutSeconds) {
        // Cliquer sur le bouton "Edit"
        WebElement editButton = waitForElement(By.id("edit-button"), timeoutSeconds);
        scrollClick(editButton);

        // Attendre que les champs éditables soient visibles
        WebElement nameEdit = waitForElement(By.cssSelector(".name-edit"), timeoutSeconds);
        WebElement descriptionEdit = waitForElement(By.cssSelector(".description-edit"), timeoutSeconds);

        // Modifier les valeurs
        nameEdit.clear();
        nameEdit.sendKeys(newName);
        descriptionEdit.clear();
        descriptionEdit.sendKeys(newDescription);

        // Ajouter une nouvelle étape
        WebElement addStepButton = waitForElement(By.id("add-step"), timeoutSeconds);
        scrollClick(addStepButton);

        WebElement stepsList = waitForElement(By.id("steps-list"), timeoutSeconds);
        WebElement newStepInput = stepsList.findElement(By.cssSelector("li:last-child .step-edit"));
        newStepInput.sendKeys(newStepToAdd);

        // Supposons que nous souhaitons supprimer une étape (ici, la deuxième étape)
        WebElement deleteStepButton = waitForElement(By.cssSelector(".delete-step"), timeoutSeconds);
        scrollClick(deleteStepButton);

        // Sauvegarder les modifications
        WebElement saveButton = waitForElement(By.id("save-button"), timeoutSeconds);
        scrollClick(saveButton);

        // Attendre le rechargement de la page de détails
        waitForUrlContains("test?id=", timeoutSeconds);
    }

    // Vérifie qu'une entrée de la liste correspond aux valeurs attendues
    public void verifyTestListEntry(int id, String expectedName, String expectedDescription, long timeoutSeconds) {
        String elementId = "test_" + id;
        WebElement nameElt = waitForElement(By.cssSelector("#" + elementId + " .name"), timeoutSeconds);
        WebElement descriptionElt = waitForElement(By.cssSelector("#" + elementId + " .description"), timeoutSeconds);
        assertNotNull(nameElt, "Aucun élément trouvé avec l'id '" + elementId + "'");
        assertNotNull(descriptionElt, "Aucun élément trouvé avec l'id '" + elementId + "'");
        assertNotEquals("", nameElt.getText(), "L'élément avec l'id '" + elementId + "' n'a pas de nom.");
        assertEquals(expectedName, nameElt.getText(), "Le nom du test ne correspond pas.");
        assertEquals(expectedDescription, descriptionElt.getText(), "La description du test ne correspond pas.");
    }

}
