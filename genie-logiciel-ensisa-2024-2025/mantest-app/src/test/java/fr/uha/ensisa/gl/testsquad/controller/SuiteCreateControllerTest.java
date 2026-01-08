package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import jakarta.servlet.http.HttpServletRequest;

public class SuiteCreateControllerTest {

    private SuiteCreateController sut;

    @Mock
    private DaoFactory dao;

    @Mock
    private TestDao testDaoMock;

    @Mock
    private SuiteTestDao suiteDaoMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new SuiteCreateController();
        sut.setDao(dao);
    }

    // Suppression de verifyNoMoreInteractions(testDaoMock) dans tearDown()
    // car certaines interactions avec testDaoMock sont attendues

    @Test
    @DisplayName("GET /suiteCreate returns suiteCreate view with tests")
    public void testCreateSuiteForm() {
        // Création d'un mock HttpServletRequest
        HttpServletRequest request = mock(HttpServletRequest.class);
        // Configurez getContextPath() pour retourner une chaîne vide (pour une
        // application déployée à la racine)
        when(request.getContextPath()).thenReturn("");

        // Assurez-vous que dao.getTestDao() retourne votre mock testDaoMock
        when(dao.getTestDao()).thenReturn(testDaoMock);

        ModelAndView mav = sut.createSuiteForm(request);

        assertNotNull(mav, "La ModelAndView ne doit pas être null");
        assertEquals("suiteCreate", mav.getViewName(), "La vue doit être 'suiteCreate'");
        assertNotNull(mav.getModel().get("tests"), "Le modèle doit contenir l'attribut 'tests'");
    }

    @Test
    @DisplayName("GET /suiteCreate handles null contextPath")
    public void testCreateSuiteFormWithNullContextPath() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn(null);
        when(dao.getTestDao()).thenReturn(testDaoMock);

        ModelAndView mav = sut.createSuiteForm(request);

        assertNotNull(mav);
        assertEquals("suiteCreate", mav.getViewName());
        assertNotNull(mav.getModel().get("tests"));
        assertEquals("", mav.getModel().get("contextPath"));
    }

    @Test
    @DisplayName("GET /suiteCreate handles contextPath literally equal to 'null'")
    public void testCreateSuiteFormWithLiteralNullContextPath() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("null");
        when(dao.getTestDao()).thenReturn(testDaoMock);

        ModelAndView mav = sut.createSuiteForm(request);

        assertNotNull(mav);
        assertEquals("suiteCreate", mav.getViewName());
        assertNotNull(mav.getModel().get("tests"));
        assertEquals("", mav.getModel().get("contextPath"));
    }

    @Test
    @DisplayName("POST /suiteCreate returns 400 redirection when suiteName is null")
    public void testCreateSuite_NullSuiteName() throws Exception {
        ResponseEntity<String> response = sut.createSuite(null, "Some description", null);
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode(), "Le status doit être SEE_OTHER");
        assertEquals("./400", response.getHeaders().getFirst("Location"), "La redirection doit être './400'");
    }

    @Test
    @DisplayName("POST /suiteCreate returns 400 redirection when suiteName is empty")
    public void testCreateSuite_EmptySuiteName() throws Exception {
        ResponseEntity<String> response = sut.createSuite("   ", "Some description", null);
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals("./400", response.getHeaders().getFirst("Location"));
    }

    @Test
    @DisplayName("POST /suiteCreate returns 400 redirection when description is null")
    public void testCreateSuite_NullDescription() throws Exception {
        ResponseEntity<String> response = sut.createSuite("Suite Name", null, null);
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals("./400", response.getHeaders().getFirst("Location"));
    }

    @Test
    @DisplayName("POST /suiteCreate persists suite without tests when suiteTestsJson is empty")
    public void testCreateSuite_EmptySuiteTestsJson() throws Exception {
        when(dao.getSuiteDao()).thenReturn(suiteDaoMock);
        ResponseEntity<String> response = sut.createSuite("Suite Name", "Description", "");
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals("./suiteList", response.getHeaders().getFirst("Location"));
        verify(suiteDaoMock).persist(any(ManualSuiteTest.class));
    }

    @Test
    @DisplayName("POST /suiteCreate persists suite with tests when suiteTestsJson is provided")
    public void testCreateSuite_WithSuiteTestsJson() throws Exception {
        when(dao.getTestDao()).thenReturn(testDaoMock);
        when(dao.getSuiteDao()).thenReturn(suiteDaoMock);
        List<Long> testIds = Arrays.asList(1L, 2L);
        ObjectMapper mapper = new ObjectMapper();
        String suiteTestsJson = mapper.writeValueAsString(testIds);
        ManualTest test1 = new ManualTest(1, "Test1", "Desc1");
        ManualTest test2 = new ManualTest(2, "Test2", "Desc2");
        when(testDaoMock.find(1L)).thenReturn(test1);
        when(testDaoMock.find(2L)).thenReturn(test2);
        ResponseEntity<String> response = sut.createSuite("Suite Name", "Description", suiteTestsJson);
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals("./suiteList", response.getHeaders().getFirst("Location"));
        verify(suiteDaoMock).persist(any(ManualSuiteTest.class));
    }

    @Test
    @DisplayName("POST /suiteCreate persists suite without tests when suiteTestsJson provided but no test found")
    public void testCreateSuite_WithSuiteTestsJson_NoTestsFound() throws Exception {
        when(dao.getTestDao()).thenReturn(testDaoMock);
        when(dao.getSuiteDao()).thenReturn(suiteDaoMock);
        List<Long> testIds = Collections.singletonList(99L);
        ObjectMapper mapper = new ObjectMapper();
        String suiteTestsJson = mapper.writeValueAsString(testIds);
        when(testDaoMock.find(99L)).thenReturn(null);
        ResponseEntity<String> response = sut.createSuite("Suite Name", "Description", suiteTestsJson);
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals("./suiteList", response.getHeaders().getFirst("Location"));
        verify(suiteDaoMock).persist(any(ManualSuiteTest.class));
    }

    @Test
    @DisplayName("POST /suiteCreate persists suite with only found tests when some test ids are not found")
    public void testCreateSuite_WithSuiteTestsJson_SomeTestsNotFound() throws Exception {
        // Préparez les mocks pour les DAO
        when(dao.getTestDao()).thenReturn(testDaoMock);
        when(dao.getSuiteDao()).thenReturn(suiteDaoMock);

        // Simuler une liste d'identifiants, dont 1 et 2 existent, 3 n'existe pas
        List<Long> testIds = Arrays.asList(1L, 2L, 3L);
        ObjectMapper mapper = new ObjectMapper();
        String suiteTestsJson = mapper.writeValueAsString(testIds);

        // Configurer le mock pour renvoyer des tests pour 1 et 2, et null pour 3
        ManualTest test1 = new ManualTest(1, "Test1", "Desc1");
        ManualTest test2 = new ManualTest(2, "Test2", "Desc2");
        when(testDaoMock.find(1L)).thenReturn(test1);
        when(testDaoMock.find(2L)).thenReturn(test2);
        when(testDaoMock.find(3L)).thenReturn(null);

        // Appeler la méthode POST du contrôleur
        ResponseEntity<String> response = sut.createSuite("Suite Name", "Description", suiteTestsJson);

        // Vérifier la réponse
        assertNotNull(response, "La réponse ne doit pas être null");
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode(), "Le status doit être SEE_OTHER");
        assertEquals("./suiteList", response.getHeaders().getFirst("Location"),
                "La redirection doit être './suiteList'");

        // Capturer la suite persistée et vérifier qu'elle contient uniquement 2 tests
        ArgumentCaptor<ManualSuiteTest> captor = ArgumentCaptor.forClass(ManualSuiteTest.class);
        verify(suiteDaoMock).persist(captor.capture());
        ManualSuiteTest persistedSuite = captor.getValue();
        assertEquals(2, persistedSuite.getTests().size(),
                "Seuls les tests trouvés (2) doivent être ajoutés à la suite");
    }

    @Test
    @DisplayName("POST /suiteCreate persists suite without tests when suiteTestsJson is null")
    public void testCreateSuite_NullSuiteTestsJson() throws Exception {
        when(dao.getSuiteDao()).thenReturn(suiteDaoMock);
        // Appel avec suiteTestsJson null
        ResponseEntity<String> response = sut.createSuite("Suite Name", "Description", null);
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals("./suiteList", response.getHeaders().getFirst("Location"));

        // Vérifier que la suite persistée ne contient aucun test
        ArgumentCaptor<ManualSuiteTest> captor = ArgumentCaptor.forClass(ManualSuiteTest.class);
        verify(suiteDaoMock).persist(captor.capture());
        ManualSuiteTest persistedSuite = captor.getValue();
        assertEquals(0, persistedSuite.getTests().size(),
                "Aucun test ne doit être ajouté lorsque suiteTestsJson est null");
    }

    @Test
    @DisplayName("POST /suiteCreate persists suite without tests when suiteTestsJson is whitespace")
    public void testCreateSuite_WhitespaceSuiteTestsJson() throws Exception {
        when(dao.getSuiteDao()).thenReturn(suiteDaoMock);
        // SuiteTestsJson contenant uniquement des espaces
        String whitespace = "   ";
        ResponseEntity<String> response = sut.createSuite("Suite Name", "Description", whitespace);
        assertNotNull(response);
        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        assertEquals("./suiteList", response.getHeaders().getFirst("Location"));

        // Vérifier que la suite persistée ne contient aucun test
        ArgumentCaptor<ManualSuiteTest> captor = ArgumentCaptor.forClass(ManualSuiteTest.class);
        verify(suiteDaoMock).persist(captor.capture());
        ManualSuiteTest persistedSuite = captor.getValue();
        assertEquals(0, persistedSuite.getTests().size(),
                "Aucun test ne doit être ajouté lorsque suiteTestsJson est vide après trim");
    }

    @Test
    @DisplayName("GET /suiteCreate/availableTests returns view with available tests")
    public void testAvailableTestsEndpoint() {
        when(dao.getTestDao()).thenReturn(testDaoMock);

        List<ManualTest> manualTests = Arrays.asList(
                new ManualTest(1, "Test1", "Desc1"),
                new ManualTest(2, "Test2", "Desc2"));
        when(testDaoMock.findAll()).thenReturn(manualTests);

        ModelAndView mav = sut.availableTests();

        assertNotNull(mav, "La ModelAndView ne doit pas être null");

        assertEquals("../fragments/suiteCreateAvailableTests.html", mav.getViewName(),
                "La vue doit être 'suiteCreateAvailableTests'");

        Object testsObj = mav.getModel().get("tests");
        assertNotNull(testsObj, "Le modèle doit contenir l'attribut 'tests'");
        assertEquals(manualTests, testsObj,
                "La liste des tests dans le modèle doit correspondre à celle renvoyée par le DAO");

        verify(testDaoMock).findAll();
    }

}
