package fr.uha.ensisa.gl.testsquad.it;


import fr.uha.ensisa.gl.testsquad.mantest.status.TestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ExecutionCreateIT extends BaseIT {

    private static final String ID_TEST = "1";
    private static final String STEP_TEST_0_CLASS = "step-item-0";
    private static final String STEP_TEST_1_CLASS = "step-item-1";
    private static final String EXECUTION_CREATE_URL = "executionCreate?id=";
    private static final String EXECUTION_CREATE_NEXT_URL = "testReport?id="; // URL to redirect to after successful submission
    private static final String EXECUTION_COMMENT_ID = "execution-comment";
    private static final String ACCEPTED_STEP_BUTTON_ID = "check-button";
    private static final String REFUSED_STEP_BUTTON_ID = "x-button";
    private static final String PREVIOUS_BUTTON_ID = "previous-button";
    private static final String STEP_COMMENT_CLASS = "step-comment";
    private static final String DEFAULT_STEP_COMMENT = "";

    private static final String DEFAULT_STEP_EXECUTED_STATUS = "UNDEFINED";
    private static final String DEFAULT_STEP_EXECUTED_COMMENT = "Step wasn't run";
    private static final String EXECUTION_DETAILS_URL = "/execution?id=";
    private static final String EXECUTION_TEST_COMMENT_CLASS = "comment-view";
    private static final String EXECUTION_TEST_STEP_COMMENT_CLASS = "step-comment-view" ;

    private static final String EXECUTION_FIRST_STEP_ACCEPTED_DESCRIPTION = "Step 1 completed";
    private static final String EXECUTION_SECOND_STEP_ACCEPTED_DESCRIPTION = "Step 2 completed";
    private static final String EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION = "Step 1 refused";

    @BeforeEach
    public void setup() {
        driver.get(getBaseUrl() + EXECUTION_CREATE_URL + ID_TEST);
    }

    public String handleAlert(long timeoutSeconds) {
        try {
            Alert alert = waitForAlert(timeoutSeconds);
            String alertText = alert.getText();
            alert.accept();
            return alertText;
        } catch (TimeoutException e) {
            // No alert shown - check if we moved to the next step or submitted the form
            if (!Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_URL + ID_TEST)) {
                // Form was submitted successfully instead of showing validation error because we are not
                // on the EXECUTION_CREATE_URL anymore
                throw (new AssertionError("Form was submitted successfully instead of showing validation error"));
            }
        }
        return null;
    }

    @Test
    public void ExecutionCreatePageLoad() throws IOException {
        int testId = 1;

        HttpURLConnection connection = (HttpURLConnection) new URL(getBaseUrl() + "executionCreate?id=" + testId).openConnection();
        connection.setRequestMethod("GET");
        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode, "Expected HTTP status code 200");
    }

    /**
     * Tests for validation errors when updating step status and comments
     *
     * @param stepComment The comment to enter for the step (can be null to leave as default)
     * @param executionComment The comment to enter for the execution (can be null to leave as default)
     * @param browserAlertExpected The boolean to indicate if the creation of the test will trigger a browser alert (if the form is not correct)
     * @param stepStatusRefused The boolean to indicate if the test will be refused or not (true = refused, false = accepted)
     * @return The error message if one is displayed, or null if no error was shown
     */
    private String testStepExecution(String stepComment, String executionComment, Boolean browserAlertExpected, Boolean stepStatusRefused) {
        // The driver should be at EXECUTION_CREATE_URL
        String allStepsClasses = "." + STEP_TEST_0_CLASS + ", ." + STEP_TEST_1_CLASS;

        // Set execution comment if provided
        if (executionComment != null) {
            WebElement executionCommentElement = driver.findElement(By.id(EXECUTION_COMMENT_ID));
            executionCommentElement.clear(); // Clear existing execution comment if any
            executionCommentElement.sendKeys(executionComment);
        }

        // Get the current visible step's status and comment fields
        WebElement visibleStep = driver.findElements(By.cssSelector(allStepsClasses))
                .stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No visible step found \n" + driver.getPageSource()));

        WebElement stepCommentField = visibleStep.findElement(By.className(STEP_COMMENT_CLASS));

        stepCommentField.clear(); // Clear existing step comment if any

        // Set step comment if provided
        if (stepComment != null) {
            stepCommentField.sendKeys(stepComment);
        }

        WebElement nextButton;
        if (stepStatusRefused) {
            // Click next button to accept the step and to trigger validation form
            nextButton = driver.findElement(By.id(REFUSED_STEP_BUTTON_ID));

        } else {
            // Click next button to accept the step and to trigger validation form
            nextButton = driver.findElement(By.id(ACCEPTED_STEP_BUTTON_ID));
        }
        scrollClick(nextButton);

        // Check for alert (browser alert) if the boolean browserAlertExpected is set to true
        if (browserAlertExpected) {
            handleAlert(2);
        }
        return null;
    }

    @Test
    public void testInitialPageState() {
        // Check initial state of fields
        WebElement executionComment = driver.findElement(By.id(EXECUTION_COMMENT_ID));
        String executionCommentValue = executionComment.getDomProperty("value");
        assertTrue(executionCommentValue == null || executionCommentValue.isEmpty(),
                "Execution comment should be empty initially");

        // Check that only the first step is visible
        WebElement visibleStep = driver.findElement(By.className(STEP_TEST_0_CLASS));
        assertTrue(visibleStep.isDisplayed(), "First step should be visible initially");

        WebElement secondStep = driver.findElement(By.className(STEP_TEST_1_CLASS));
        assertFalse(secondStep.isDisplayed(), "Second step should not be visible initially");

        // Check comment of first step
        WebElement stepCommentField = visibleStep.findElement(By.className(STEP_COMMENT_CLASS));
        assertEquals(DEFAULT_STEP_COMMENT, stepCommentField.getDomProperty("value"),
                "Default step comment should be : " + DEFAULT_STEP_COMMENT);
    }

    @Test
    public void testStepEmptyComment() {
        // Test with empty step comment and an execution comment
        String errorMessage = testStepExecution("", "Valid execution comment", false, false);
        assertNull(errorMessage, "Should not show error for empty step comment");
    }

    @Test
    public void testStepDefaultComment() {
        // Test with default step comment and an execution comment
        String errorMessage = testStepExecution(DEFAULT_STEP_COMMENT, "Valid execution comment", false, false);
        assertNull(errorMessage, "Should not show error when step comment is set to default : " + DEFAULT_STEP_COMMENT);
    }

    @Test
    public void testStepCustomComment() {
        // Test with custom comment and an execution comment
        String customStepComment = "Custom step comment";
        String errorMessage = testStepExecution(customStepComment, "Valid execution comment", false, false);
        assertNull(errorMessage, "Should not show error when step comment is a custom one, here it is : " + customStepComment);
    }

    @Test
    public void testStepEmptyCommentExecutionComment() {
        // Test with empty step comment and an execution comment
        String errorMessage = testStepExecution("", "Valid execution comment", false, false);
        assertNull(errorMessage, "Should not show error for empty step comment");
    }

    @Test
    public void testStepDefaultCommentExecutionComment() {
        // Test with default step comment and an execution comment
        String errorMessage = testStepExecution(DEFAULT_STEP_COMMENT, "Valid execution comment", false, false);
        assertNull(errorMessage, "Should not show error when step comment is set to default : " + DEFAULT_STEP_COMMENT);
    }

    @Test
    public void testStepCustomCommentExecutionComment() {
        // Test with custom comment and an execution comment
        String customStepComment = EXECUTION_FIRST_STEP_ACCEPTED_DESCRIPTION;
        String errorMessage = testStepExecution(customStepComment, "Valid execution comment", false, false);
        assertNull(errorMessage, "Should not show error when step comment is a custom one, here it is : " + customStepComment);
    }

    @Test
    public void testSuccessfulStepTestNavigation() {
        // Test successful navigation between steps with valid data (check that we're on the second step after
        // first step) and move to the previous step to check navigation

        // First step (with a custom step comment and no execution comment)
        String errorMessage = testStepExecution(DEFAULT_STEP_COMMENT, null, false, false);
        assertNull(errorMessage, "Should not show error and proceed to next step");

        // Check we're now showing the second step
        WebElement secondStep = driver.findElement(By.className(STEP_TEST_1_CLASS));

        assertTrue(secondStep.isDisplayed(), "Second step should be visible after navigating");

        // Click previous button
        WebElement previousButton = driver.findElement(By.id(PREVIOUS_BUTTON_ID));
        scrollClick(previousButton);

        // Check we're back on first step
        WebElement firstStep = driver.findElement(By.className(STEP_TEST_0_CLASS));
        assertTrue(firstStep.isDisplayed(), "First step should be visible after clicking previous");
    }

    @Test
    public void testCompleteRefusedSubmission() {
        // Test for REFUSED status on the last step and check that the form is not submitted because
        // there is no step comment (even if there is an execution comment)

        // First step
        testStepExecution(EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION, null, false, false);

        // Second step (last step)
        testStepExecution(null, "This is an execution comment", true, true);

        // The form should not be submitted because the execution comment is empty
        assertEquals(driver.getCurrentUrl(), getBaseUrl() + EXECUTION_CREATE_URL + ID_TEST);
    }

    @Test
    public void testRefusedSubmission() {
        // Test for REFUSED status on the first step and check that the form is not submitted because
        // there is no step comment (even if there is an execution comment)

        // First step
        testStepExecution(null, "This is an execution comment", true, true);

        // The form should not be submitted because the execution comment is empty
        assertEquals(driver.getCurrentUrl(), getBaseUrl() + EXECUTION_CREATE_URL + ID_TEST);
    }

    @Test
    public void testSuccessfulCompleteRefusedSubmission() {
        // Test for REFUSED status on the last step and check that the form is submitted because
        // there is a step comment

        // First step
        testStepExecution(EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION, null, false, false);

        // Second step (last step)
        testStepExecution("Step 2 is refused because why not", "The test is refused because why not", false, true);

        // Check we're redirected to the test list page
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST,5);

        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL +ID_TEST),
                "Should be redirected to test list after successful submission");

    }

    @Test
    public void testSuccessfulRefusedSubmission() {
        // Test for REFUSED status on the first step and check that the form is submitted because
        // there is a step comment

        // First step
        testStepExecution("Step 1 is refused because why not", "The test is refused because why not", false, true);

        // Check we're redirected to the test list page
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST,5);



        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL +ID_TEST),
                "Should be redirected to test list after successful submission");
    }

    @Test
    public void testEmptyExecutionComment() {
        // Complete all steps with valid data but leave execution comment empty and submit the form correctly
        // First step
        testStepExecution(EXECUTION_FIRST_STEP_ACCEPTED_DESCRIPTION, null, false, false);

        // Second step (last step)
        testStepExecution( EXECUTION_SECOND_STEP_ACCEPTED_DESCRIPTION, null, false, false);

        // The form should be submitted because the execution comment is empty so we should advance to the next page
        // Check we're redirected to the test list page
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST,5);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL +ID_TEST),
                "Should be redirected to test list after successful submission");
    }

    @Test
    public void testCompleteAcceptedSubmission() {
        // First step
        testStepExecution( EXECUTION_FIRST_STEP_ACCEPTED_DESCRIPTION, null, false, false);

        // Second step
        testStepExecution( EXECUTION_SECOND_STEP_ACCEPTED_DESCRIPTION, "The test is great, good job!", false, false);

        // Check we're redirected to the test list page
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST,5);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL +ID_TEST),
                "Should be redirected to test list after successful submission");
    }

    public void executionsTest() {
        // Test for REFUSED status on the last step and check that the form is submitted because
        // there is a step comment

        // First step (first execution of the test)
        testStepExecution(EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION, null, false, true);

        // Check we're redirected to the test list page
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST,5);

        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL +ID_TEST),
                "Should be redirected to test list after successful submission");

        navigateTo(EXECUTION_CREATE_URL + ID_TEST);

        // Check we're redirected to the test execution page
        waitForUrlContains(EXECUTION_CREATE_URL + ID_TEST,5);

        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_URL +ID_TEST),
                "Should be redirected to test execution page after successful submission");

        // New test execution with valid data and ACCEPTED status (second execution of the test)
        testStepExecution(EXECUTION_FIRST_STEP_ACCEPTED_DESCRIPTION, null, false, false);

        // Second step (second execution of the test)
        testStepExecution(EXECUTION_SECOND_STEP_ACCEPTED_DESCRIPTION, "TEST PASSED", false, false);

        // Check we're redirected to the test list page
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST,5);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL +ID_TEST),
                "Should be redirected to test list after successful submission");

        navigateTo(EXECUTION_CREATE_URL + ID_TEST);

        // Check we're redirected to the test execution page
        waitForUrlContains(EXECUTION_CREATE_URL + ID_TEST,5);

        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_URL +ID_TEST),
                "Should be redirected to test execution page after successful submission");

        // New test execution with valid data and REFUSED status (third execution of the test)
        testStepExecution(EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION, "TEST IN REGRESSION", false, true);

        // Check we're redirected to the test list page
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST,5);

        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL +ID_TEST),
                "Should be redirected to test list after successful submission");
    }

    /**
     * Verifies the test list entry at the specified index with the expected status and total executions.
     *
     * @param indexInList The index of the test in the list (0-based, 0 being the first in the list so the more recent
     *                   execution of the test).
     * @param testStatus  The expected status of the test.
     */
    public void verifyTestExecutionsListEntry(int indexInList, TestStatus testStatus) {
        // Wait for the test list page to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Wait for the DOM content to be loaded
        wait.until(driver -> Objects.equals(((JavascriptExecutor) driver).executeScript("return document.readyState"), "complete"));

        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL + ID_TEST));

        // Find all test item elements
        List<WebElement> executionItems = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("li.test-item")));

        // Verify specific test execution at the given index
        WebElement targetExecution = executionItems.get(indexInList);

        // Verify execution status
        WebElement statusElement = wait.until(ExpectedConditions.visibilityOf(targetExecution.findElement(By.cssSelector("p span.status"))));
        String expectedStatusText;

        switch (testStatus) {
            case PASSED:
                expectedStatusText = "PASSED";
                break;
            case FAILED:
                expectedStatusText = "FAILED";
                break;
            case REGRESSION:
                expectedStatusText = "REGRESSION";
                break;
            default:
                throw new IllegalArgumentException("Unexpected test status: " + testStatus);
        }

        assertEquals(expectedStatusText, statusElement.getText(),
                "Execution status should match the expected status, got :" +  statusElement.getText() + " instead of " + expectedStatusText);

        // Verify step results
        List<WebElement> stepResults = targetExecution.findElements(By.cssSelector("ul.step-results > li"));
        assertFalse(stepResults.isEmpty(), "Step results should not be empty");

        // Verify execution timestamp is present
        WebElement executionTimeElement = targetExecution.findElement(By.cssSelector("p a.execution-link"));
        assertNotNull(executionTimeElement.getText(), "Execution timestamp should be present");
    }

    /**
     * Verifies the test step list entries at the specified index with the expected status and expected description
     * based on the tests executed by the executionsTest method.
     *
     * @param indexInList The index of the test in the list (0-based, 0 being the first in the list so the more recent
     *                   execution of the test).
     * @param stepStatusRefused  The expected status of the test (true if the execution of the test has been refused).
     */
    private void verifyTestStepExecutionsListEntry(int indexInList, Boolean stepStatusRefused) {
        // Wait for the test list page to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Find all test item elements
        List<WebElement> executionItems = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("li.test-item")));

        // Verify specific test execution at the given index
        WebElement targetExecution = executionItems.get(indexInList);

        List<WebElement> stepResults = targetExecution.findElements(By.cssSelector("ul.step-results > li"));
        assertFalse(stepResults.isEmpty(), "Step results should not be empty");

        WebElement firstStepStatusElement = wait.until(ExpectedConditions.visibilityOf(targetExecution.findElement(By.cssSelector("span.step-status"))));
        WebElement firstStepDescriptionElement = wait.until(ExpectedConditions.visibilityOf(targetExecution.findElement(By.cssSelector("ul.step-results > li span:not([class])"))));
        WebElement secondStepStatusElement = wait.until(ExpectedConditions.visibilityOf(targetExecution.findElement(By.cssSelector("ul.step-results > li:nth-child(2) span.step-status"))));
        WebElement secondStepDescriptionElement = wait.until(ExpectedConditions.visibilityOf(targetExecution.findElement(By.cssSelector("ul.step-results > li:nth-child(2) span:not([class])"))));
        if (stepStatusRefused) {
            // Check the status of the first step
            String firstStepExpectedStatusText = "REFUSED";
            assertEquals(firstStepExpectedStatusText, firstStepStatusElement.getText(), "Status of the first step should be" + firstStepExpectedStatusText);

            // Check the description of the first step
            String firstStepExpectedDescription = EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION;
            assertEquals(firstStepExpectedDescription, firstStepDescriptionElement.getText(), "Description of the first step should be" + firstStepExpectedDescription);

            // Check the status of the second step
            String secondStepExpectedStatusText = "UNDEFINED";
            assertEquals(secondStepExpectedStatusText, secondStepStatusElement.getText(), "Status of the first step should be" + secondStepExpectedStatusText);

            // Check the description of the second step
            String secondStepExpectedDescription = DEFAULT_STEP_EXECUTED_COMMENT;
            assertEquals(secondStepExpectedDescription, secondStepDescriptionElement.getText(), "Description of the second step should be" + secondStepExpectedDescription);
        } else {
            // Check the status of the first step
            String firstStepExpectedStatusText = "ACCEPTED";
            assertEquals(firstStepExpectedStatusText, firstStepStatusElement.getText(), "Status of the first step should be" + firstStepExpectedStatusText);

            // Check the description of the first step
            String firstStepExpectedDescription = EXECUTION_FIRST_STEP_ACCEPTED_DESCRIPTION;
            assertEquals(firstStepExpectedDescription, firstStepDescriptionElement.getText(), "Description of the first step should be" + firstStepExpectedDescription);

            // Check the status of the second step
            String secondStepExpectedStatusText = "ACCEPTED";
            assertEquals(secondStepExpectedStatusText, secondStepStatusElement.getText(), "Status of the second step should be" + secondStepExpectedStatusText);

            // Check the description of the second step
            String secondStepExpectedDescription = EXECUTION_SECOND_STEP_ACCEPTED_DESCRIPTION;
            assertEquals(secondStepExpectedDescription, secondStepDescriptionElement.getText(), "Description of the second step should be" + secondStepExpectedDescription);
        }
    }

    private void testingExecutionDetails(String expectedName, TestStatus expectedStatus, String expectedExecutionComment, List<String[]> expectedSteps) {
        // Wait for the execution details page to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Verify test name
        WebElement nameElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//p[contains(text(), 'Name:')]/span")));
        assertEquals(expectedName, nameElement.getText(), "Test name should match");

        // Verify execution status
        WebElement statusElement = driver.findElement(By.xpath("//p[contains(text(), 'Status:')]/span/span"));
        String expectedStatusText;
        switch (expectedStatus) {
            case PASSED:
                expectedStatusText = "Passed";
                break;
            case FAILED:
                expectedStatusText = "Failed";
                break;
            case REGRESSION:
                expectedStatusText = "Regression";
                break;
            default:
                throw new IllegalArgumentException("Unexpected test status: " + expectedStatus);
        }
        assertEquals(expectedStatusText, statusElement.getText(), "Execution status should match");

        // Verify execution comment
        if (expectedExecutionComment != null) {
            WebElement executionCommentElement = driver.findElement(By.className(EXECUTION_TEST_COMMENT_CLASS));
            assertEquals(expectedExecutionComment, executionCommentElement.getText(), "Execution comment should " +
                    "match, we got :" + executionCommentElement.getText() + "instead of :" + expectedExecutionComment);
        }

        // Verify steps
        List<WebElement> stepItems = driver.findElements(By.cssSelector("li.step-item"));
        assertEquals(expectedSteps.size(), stepItems.size(), "Number of steps should match");

        // Iterate through expected steps and verify details
        for (int i = 0; i < expectedSteps.size(); i++) {
            WebElement stepItem = stepItems.get(i);
            String[] expectedStep = expectedSteps.get(i);

            // Verify step description (first array element)
            WebElement stepDescriptionElement = stepItem.findElement(By.xpath(".//p[contains(text(), 'Step:')]/span"));
            assertEquals(expectedStep[0], stepDescriptionElement.getText(),
                    "Step description should match for step " + (i + 1));

            // Verify step status (second array element)
            WebElement stepStatusElement = stepItem.findElement(By.cssSelector(".step-status > span"));
            assertEquals(expectedStep[1], stepStatusElement.getText(),
                    "Step status should match for step " + (i + 1));

            // Verify step comment (third array element)
            WebElement stepCommentElement = stepItem.findElement(By.cssSelector(".step-comment-view"));
            assertEquals(expectedStep[2], stepCommentElement.getText(),
                    "Step comment should match for step " + (i + 1));
        }

        WebElement testReportLink = driver.findElement(By.className("test-report")); // Find the button to redirect to testReport.html from executionDetails.html

        // Goes to testReport
        scrollClick(testReportLink);
        waitForUrlContains(EXECUTION_CREATE_NEXT_URL + ID_TEST , 5);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_CREATE_NEXT_URL + ID_TEST));
    }


    @Test
    public void testingReportIT() {
        // Do 3 executions of the tests
        executionsTest();

        int expectedTotalExecutions = 3;

        // Verify the 3 test execution list entries (must be on the EXECUTION_CREATE_NEXT_URL page before continuing)
        verifyTestExecutionsListEntry(2, TestStatus.FAILED); // First execution of the test
        verifyTestExecutionsListEntry(1, TestStatus.PASSED); // Second execution of the test
        verifyTestExecutionsListEntry(0, TestStatus.REGRESSION); // Third execution of the test
        verifyTestStepExecutionsListEntry(2, true); // First execution of the test
        verifyTestStepExecutionsListEntry(1, false); // Second execution of the test
        verifyTestStepExecutionsListEntry(0, true); // Third execution of the test

        // Verify the details of the 3 test execution list entries
        String testName = "Test" + ID_TEST;

        // Navigate from the page EXECUTION_CREATE_NEXT_URL to EXECUTION_DETAILS_URL
        WebElement stepExecutionLink1 = driver.findElement(By.cssSelector("#execution_1 .execution-link"));
        scrollClick(stepExecutionLink1);
        waitForUrlContains(EXECUTION_DETAILS_URL + 1 , 5);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_DETAILS_URL + 1));

        // Prepare expected steps of the first execution of the test
        List<String[]> expectedSteps1 = Arrays.asList(
                new String[]{"Step_1", "Refused", EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION},
                new String[]{"Step_2", "Undefined", DEFAULT_STEP_EXECUTED_COMMENT}
        );

        String executionComment1 = null;

        // Call the verification method on the first execution of the test (must be on the EXECUTION_DETAILS_URL page before continuing)
        testingExecutionDetails(
                testName,         // Expected name
                TestStatus.FAILED,     // Expected status
                executionComment1,      // Expected execution comment
                expectedSteps1         // Expected steps
        );

        // Navigate from the page EXECUTION_CREATE_NEXT_URL to EXECUTION_DETAILS_URL
        WebElement stepExecutionLink2 = driver.findElement(By.cssSelector("#execution_2 .execution-link"));
        scrollClick(stepExecutionLink2);
        waitForUrlContains(EXECUTION_DETAILS_URL + 2 , 5);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_DETAILS_URL + 2));

        String executionComment2 = "TEST PASSED";

        // Prepare expected steps of the second execution of the test
        List<String[]> expectedSteps2 = Arrays.asList(
                new String[]{"Step_1", "Accepted", EXECUTION_FIRST_STEP_ACCEPTED_DESCRIPTION},
                new String[]{"Step_2", "Accepted", EXECUTION_SECOND_STEP_ACCEPTED_DESCRIPTION}
        );

        // Call the verification method on the second execution of the test (must be on the EXECUTION_DETAILS_URL page before continuing)
        testingExecutionDetails(
                testName,         // Expected name
                TestStatus.PASSED,     // Expected status
                executionComment2,      // Expected execution comment
                expectedSteps2         // Expected steps
        );

        // Navigate from the page EXECUTION_CREATE_NEXT_URL to EXECUTION_DETAILS_URL
        WebElement stepExecutionLink3 = driver.findElement(By.cssSelector("#execution_3 .execution-link"));
        scrollClick(stepExecutionLink3);
        waitForUrlContains(EXECUTION_DETAILS_URL + 3 , 5);
        assertTrue(Objects.requireNonNull(driver.getCurrentUrl()).contains(EXECUTION_DETAILS_URL + 3));

        String executionComment3 = "TEST IN REGRESSION";

        // Prepare expected steps of the third execution of the test (must be on the EXECUTION_DETAILS_URL page before continuing)
        List<String[]> expectedSteps3 = Arrays.asList(
                new String[]{"Step_1", "Refused", EXECUTION_FIRST_STEP_REFUSED_DESCRIPTION},
                new String[]{"Step_2", "Undefined", DEFAULT_STEP_EXECUTED_COMMENT}
        );

        // Call the verification method on the third execution of the test
        testingExecutionDetails(
                testName,         // Expected name
                TestStatus.REGRESSION,     // Expected status
                executionComment3,      // Expected execution comment
                expectedSteps3         // Expected steps
        );

    }
}
