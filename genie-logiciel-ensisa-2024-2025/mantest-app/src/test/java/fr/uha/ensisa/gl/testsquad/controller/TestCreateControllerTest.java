package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;

public class TestCreateControllerTest {

    private TestCreateController sut;

    @Mock
    private DaoFactory dao;

    @Mock
    private TestDao testDaoMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new TestCreateController();
        sut.setDao(dao);
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(testDaoMock);
    }

    @Test
    public void createGetTest() {
        ModelAndView result = sut.create(false);
        assertNotNull(result);
        assertEquals("testCreate", result.getViewName());
        // On peut vérifier que l'attribut "embedded" est bien passé (ici false)
        assertEquals(false, result.getModel().get("embedded"));
    }

    @Test
    public void createPostTestNullName() throws Exception {
        when(dao.getTestDao()).thenReturn(testDaoMock);
        ModelAndView result = sut.create(null, null, null, false);
        assertNotNull(result);
        // Vérification de la redirection vers /400 en cas de suiteName invalide
        assertEquals("redirect:/400", result.getViewName());
    }

    @Test
    public void createPostTestEmptySuiteName() throws Exception {
        when(dao.getTestDao()).thenReturn(testDaoMock);
        ModelAndView result = sut.create("   ", null, null, false);
        assertNotNull(result);
        assertEquals("redirect:/400", result.getViewName());
    }

    @Test
    public void createPostTestNullDescription() throws Exception {
        String testName = "This is a test name for testing";
        when(dao.getTestDao()).thenReturn(testDaoMock);
        ModelAndView result = sut.create(testName, null, null, false);
        assertNotNull(result);
        assertEquals("redirect:/400", result.getViewName());
    }

    @Test
    public void createPostTestNullJsonSteps() throws Exception {
        String testName = "This is a test name for testing";
        when(dao.getTestDao()).thenReturn(testDaoMock);
        ModelAndView result = sut.create(testName, null, "", false);
        assertNotNull(result);
        assertEquals("redirect:/400", result.getViewName());
    }

    @Test
    public void createPostTestEmptyJsonSteps() throws Exception {
        String testName = "This is a test name for testing";
        when(dao.getTestDao()).thenReturn(testDaoMock);
        ModelAndView result = sut.create(testName, "", "", false);
        assertNotNull(result);
        assertEquals("redirect:/400", result.getViewName());
    }

    @Test
    public void createPostTestNecessary() throws Exception {
        String testName = "This is a test name for testing";
        List<String> items = List.of("step1 for testing", "step2 for testing", "step3 for testing");

        // Convertir la liste en chaîne JSON
        ObjectMapper mapper = new ObjectMapper();
        String testStepsJson = mapper.writeValueAsString(items);

        when(dao.getTestDao()).thenReturn(testDaoMock);

        ModelAndView result = sut.create(testName, testStepsJson, "", false);
        assertNotNull(result);
        assertEquals("redirect:/testList", result.getViewName());

        verify(testDaoMock).persist(any(ManualTest.class));
    }

    @Test
    public void createPostTestComplete() throws Exception {
        String testName = "This is a test name for testing";
        List<String> items = List.of("step1 for testing", "step2 for testing", "step3 for testing");
        String description = "This is a test description for testing";

        // Convertir la liste en chaîne JSON
        ObjectMapper mapper = new ObjectMapper();
        String testStepsJson = mapper.writeValueAsString(items);

        when(dao.getTestDao()).thenReturn(testDaoMock);

        ModelAndView result = sut.create(testName, testStepsJson, description, false);
        assertNotNull(result);
        assertEquals("redirect:/testList", result.getViewName());

        verify(testDaoMock).persist(any(ManualTest.class));
    }

    @Test
    @DisplayName("POST /testCreate returns testCreateSuccess view when embedded is true")
    public void createPostTestEmbedded() throws Exception {
        String testName = "Embedded Test";
        List<String> items = List.of("embedded step1", "embedded step2");
        String description = "Embedded description";

        ObjectMapper mapper = new ObjectMapper();
        String testStepsJson = mapper.writeValueAsString(items);

        when(dao.getTestDao()).thenReturn(testDaoMock);

        // Appel avec embedded = true
        ModelAndView result = sut.create(testName, testStepsJson, description, true);
        assertNotNull(result);
        assertEquals("testCreateSuccess", result.getViewName());
        // Vérifier que l'attribut embedded est transmis dans le modèle
        assertEquals(true, result.getModel().get("embedded"));

        verify(testDaoMock).persist(any(ManualTest.class));
    }

    @Test
    @DisplayName("POST /testCreate returns 400 redirection when testName is an empty string")
    public void createPostTest_EmptyStringTestName() throws Exception {
        // Ici, on passe "" exactement pour testName,
        // afin que testName.isEmpty() retourne true.
        when(dao.getTestDao()).thenReturn(testDaoMock);
        // Pour ne pas déclencher d'autres conditions, on passe un JSON valide pour
        // testStepsJson et une description non nulle.
        ModelAndView result = sut.create("", "[]", "Valid description", false);
        assertNotNull(result, "La réponse ne doit pas être null");
        assertEquals("redirect:/400", result.getViewName(),
                "La redirection doit être vers '/400' lorsque testName est vide");
    }

}
