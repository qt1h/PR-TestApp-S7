package fr.uha.ensisa.gl.testsquad.ecobench;

import fr.uha.ensisa.eco.metrologie.extension.annotations.*;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Random;

@EcoDocker(network = "mantest-app-metrologie", clean = true)
@EcoDockerContainer(id = "mantest-app-proxy-1", port = 8081)
@EcoMonitor(containerId = "mantest-app-tomcat-1")
@EcoWebDriver(remote = true) // <- change this to use local browser
@EcoEnergyCounter(type = EcoEnergyCounterType.POWERSPY, name = "Main", endPoint = "$POWERSPY_HOST$")
@EcoGatling(userCount = 100, rampDuration = 10)

public class ViewTestBenchTest extends BaseBenchTest {

    private final Random rnd = new Random();

    @RepeatedTest(5)
    public void viewTests(WebDriver wb){

        final int NOMBRE_TEST_TEST = 5;
        //en secondes
        final double TEMPS_PAR_TEST = 0.5;
        final double TEMPS_RECHERCHE_TEST = 0.15;
        final double TEMPS_HOME = 0.5;

        JavascriptExecutor js = (JavascriptExecutor) wb;
        int nb;
        try {

            wb.get("/home");
            Thread.sleep((long) (TEMPS_HOME*1000));

            for(int i = 0; i< NOMBRE_TEST_TEST; i++){

                wb.get("/testList");
                js.executeScript("window.scrollBy(-2000,0)", "");

                List<WebElement> tests = wb.findElements(By.className("test-link"));
                nb=rnd.nextInt(tests.size());
                tests.get(nb).click();

                Thread.sleep((long) (TEMPS_PAR_TEST*300));
                js.executeScript("window.scrollTo(0,document.body.scrollHeight)", "");
                Thread.sleep((long) (TEMPS_PAR_TEST*500));
                js.executeScript("window.scrollTo(0,0)", "");
                Thread.sleep((long) (TEMPS_PAR_TEST*200));

                wb.get("/testList");
                js.executeScript("window.scrollBy(-2500,0)", "");
                Thread.sleep((long) (TEMPS_RECHERCHE_TEST*1000));

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RepeatedTest(5)
    public void viewReports(WebDriver wb) {

        final int NOMBRE_RAPORT_TEST = 5;
        //en secondes
        final double TEMPS_PAR_RAPORT = 0.5;
        final double TEMPS_PAR_EXECUTION = 0.5;
        final double TEMPS_RECHERCHE_RAPORT = 0.15;
        final double TEMPS_HOME = 0.5;

        JavascriptExecutor js = (JavascriptExecutor) wb;
        int nb;
        try {

            wb.get("/home");
            Thread.sleep((long) (TEMPS_HOME*1000));

            for (int i = 0; i < NOMBRE_RAPORT_TEST; i++) {

                wb.get("/report");
                js.executeScript("window.scrollBy(-2000,0)", "");

                List<WebElement> reports = wb.findElements(By.className("report-link"));
                nb = rnd.nextInt(reports.size());
                reports.get(nb).click();

                Thread.sleep((long) (TEMPS_PAR_RAPORT * 300));
                js.executeScript("window.scrollTo(0,document.body.scrollHeight)", "");
                Thread.sleep((long) (TEMPS_PAR_RAPORT * 500));
                js.executeScript("window.scrollTo(0,0)", "");
                Thread.sleep((long) (TEMPS_PAR_RAPORT * 200));

                List<WebElement> executions = wb.findElements(By.className("execution-link"));
                nb = rnd.nextInt(executions.size());
                executions.get(nb).click();

                Thread.sleep((long) (TEMPS_PAR_EXECUTION * 300));
                js.executeScript("window.scrollTo(0,document.body.scrollHeight)", "");
                Thread.sleep((long) (TEMPS_PAR_EXECUTION * 500));
                js.executeScript("window.scrollTo(0,0)", "");
                Thread.sleep((long) (TEMPS_PAR_EXECUTION * 200));

                wb.get("/report");
                js.executeScript("window.scrollBy(-2500,0)", "");
                Thread.sleep((long) (TEMPS_RECHERCHE_RAPORT * 1000));

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
