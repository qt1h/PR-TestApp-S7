package fr.uha.ensisa.gl.testsquad.it;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class HomeIT extends BaseIT {

    @Test
    public void testHomePageLoads() throws IOException {
        // Navigation vers la page d'accueil et vérification du titre
        navigateAndAssertTitle("home", "Test Application", 10);
        
        // Vérification du code HTTP de la page d'accueil
        int statusCode = getHTTPStatusCode(getBaseUrl() + "home");
        assertEquals(200, statusCode, "Expected HTTP status code 200");
    }

    @Test
    public void testClickLinkReport() {
        navigateTo("home");
        clickLinkAndAssertNavigation("report_link", "/report", 10);
    }

    @Test
    public void testClickLinkCreate() {
        navigateTo("home");
        clickLinkAndAssertNavigation("scroll_create", "/home", 10);
        clickLinkAndAssertNavigation("create_link", "/testCreate", 10);
    }

    @Test
    public void suiteClickLinkCreate() {
        navigateTo("home");
        clickLinkAndAssertNavigation("scroll_create", "/home", 10);
        clickLinkAndAssertNavigation("create_suite_link", "/suiteCreate", 10);
    }

    @Test
    public void testClickLinkList() {
        navigateTo("home");
        clickLinkAndAssertNavigation("scroll_list", "/home", 10);
        clickLinkAndAssertNavigation("list_link", "/testList", 10);
    }

    @Test
    public void suiteClickLinkList() {
        navigateTo("home");
        clickLinkAndAssertNavigation("scroll_list", "/home", 10);
        clickLinkAndAssertNavigation("list_suite_link", "/suiteList", 10);
    }

}
