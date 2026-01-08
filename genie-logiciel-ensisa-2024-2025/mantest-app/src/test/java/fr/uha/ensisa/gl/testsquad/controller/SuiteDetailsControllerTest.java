package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;

public class SuiteDetailsControllerTest {

    private SuiteDetailsController sut;

    @Mock
    private DaoFactory dao;
    @Mock
    private SuiteTestDao suiteDao;
    @Mock
    private TestDao testDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new SuiteDetailsController();
        sut.setDao(dao);

        when(dao.getSuiteDao()).thenReturn(suiteDao);
        when(dao.getTestDao()).thenReturn(testDao);
    }

    @Test
    public void testViewSuiteDetails_Found() {
        ManualSuiteTest suite = new ManualSuiteTest(1, "Suite 1", "Description");
        ManualTest t1 = new ManualTest(1, "T1", "");
        ManualTest t2 = new ManualTest(2, "T2", "");

        suite.addTest(t1);
        when(suiteDao.find(1L)).thenReturn(suite);
        when(testDao.findAll()).thenReturn(List.of(t1, t2));

        ModelAndView mav = sut.viewSuiteDetails(1L);
        assertEquals("suiteDetails", mav.getViewName());
        assertEquals(suite, mav.getModel().get("suite"));
        assertEquals(List.of(t1), mav.getModel().get("tests"));
        assertEquals(List.of(t2), mav.getModel().get("availableTests"));
    }

    @Test
    public void testViewSuiteDetails_NotFound() {
        when(suiteDao.find(999L)).thenReturn(null);

        ModelAndView mav = sut.viewSuiteDetails(999L);
        assertEquals("suiteDetails", mav.getViewName());
        assertEquals("Suite not found", mav.getModel().get("errorMessage"));
    }

    @Test
    public void testDeleteSuite_Exists() {
        ManualSuiteTest suite = new ManualSuiteTest(1, "Suite", "Desc");
        when(suiteDao.find(1L)).thenReturn(suite);

        ResponseEntity<String> resp = sut.deleteSuite(1L);
        assertEquals(HttpStatus.SEE_OTHER, resp.getStatusCode());
        assertTrue(resp.getHeaders().getFirst("Location").contains("suiteList"));
        verify(suiteDao).remove(1L);
    }

    @Test
    public void testDeleteSuite_NotFound() {
        when(suiteDao.find(2L)).thenReturn(null);

        ResponseEntity<String> resp = sut.deleteSuite(2L);
        assertEquals(HttpStatus.SEE_OTHER, resp.getStatusCode());
        assertTrue(resp.getHeaders().getFirst("Location").contains("404"));
    }

    @Test
    public void testUpdateSuite_InvalidName() {
        ManualSuiteTest suite = new ManualSuiteTest();
        when(suiteDao.find(1L)).thenReturn(suite);

        ResponseEntity<String> resp = sut.updateSuite(1L, "   ", "desc", List.of(1L));
        assertTrue(resp.getHeaders().getFirst("Location").contains("400"));
    }

    @Test
    public void testUpdateSuite_RemovesOldTests() {
        // Arrange
        ManualSuiteTest suite = new ManualSuiteTest(1, "Old", "Old");
        ManualTest oldTest = new ManualTest(99, "OldTest", "");
        ManualTest newTest = new ManualTest(10, "NewTest", "");

        suite.addTest(oldTest); // Ajoute un test initial

        when(suiteDao.find(1L)).thenReturn(suite);
        when(testDao.find(10L)).thenReturn(newTest);

        // Act
        ResponseEntity<String> resp = sut.updateSuite(1L, "Updated", "Updated", List.of(10L));

        // Assert
        assertEquals(1, suite.getTests().size());
        assertTrue(suite.getTests().contains(newTest));
        assertFalse(suite.getTests().contains(oldTest));
    }

    @Test
    @DisplayName("POST /updateSuite: suite introuvée renvoie 404")
    public void testUpdateSuite_NotFound() {
        when(suiteDao.find(99L)).thenReturn(null);
        ResponseEntity<String> resp = sut.updateSuite(99L, "Name", "Desc", List.of(1L));
        assertTrue(resp.getHeaders().getFirst("Location").contains("404"),
                "La redirection doit contenir '404' si la suite n'est pas trouvée");
    }

    @Test
    @DisplayName("POST /updateSuite: nom null renvoie 400")
    public void testUpdateSuite_NameIsNull_ShouldReturn400() {
        ManualSuiteTest suite = new ManualSuiteTest();
        when(suiteDao.find(1L)).thenReturn(suite);
        ResponseEntity<String> resp = sut.updateSuite(1L, null, "desc", List.of(1L));
        assertTrue(resp.getHeaders().getFirst("Location").contains("400"),
                "La redirection doit contenir '400' si le nom est null");
    }

    @Test
    @DisplayName("POST /updateSuite: description null renvoie 400")
    public void testUpdateSuite_DescriptionIsNull_ShouldReturn400() {
        ManualSuiteTest suite = new ManualSuiteTest();
        when(suiteDao.find(1L)).thenReturn(suite);
        ResponseEntity<String> resp = sut.updateSuite(1L, "valid", null, List.of(1L));
        assertTrue(resp.getHeaders().getFirst("Location").contains("400"),
                "La redirection doit contenir '400' si la description est null");
    }

    @Test
    @DisplayName("POST /updateSuite: nom vide ou blanc renvoie 400")
    public void testUpdateSuite_NameIsBlank_ShouldReturn400() {
        ManualSuiteTest suite = new ManualSuiteTest();
        when(suiteDao.find(1L)).thenReturn(suite);
        ResponseEntity<String> resp = sut.updateSuite(1L, "   ", "desc", List.of(1L));
        assertTrue(resp.getHeaders().getFirst("Location").contains("400"),
                "La redirection doit contenir '400' si le nom est vide ou blanc");
    }

    @Test
    @DisplayName("POST /updateSuite: mise à jour sans testIds (null) supprime tous les tests existants")
    public void testUpdateSuite_WithNullTestIds() {
        // Prépare une suite avec un test existant
        ManualSuiteTest suite = new ManualSuiteTest(1, "Suite", "Desc");
        ManualTest existingTest = new ManualTest(10, "Test10", "Desc");
        suite.addTest(existingTest);
        when(suiteDao.find(1L)).thenReturn(suite);

        // Appeler updateSuite avec testIds null
        ResponseEntity<String> resp = sut.updateSuite(1L, "Updated", "Updated Desc", null);

        // Les tests existants doivent être supprimés
        assertEquals(0, suite.getTests().size(), "Aucun test ne doit être présent lorsque testIds est null");
        verify(suiteDao).update(suite);
        assertTrue(resp.getHeaders().getFirst("Location").contains("suiteDetails?id=1"),
                "La redirection doit pointer vers suiteDetails avec l'identifiant de la suite");
    }

    @Test
    @DisplayName("POST /updateSuite: mise à jour valide avec testIds renvoie une suite mise à jour")
    public void testUpdateSuite_Valid() {
        ManualSuiteTest suite = new ManualSuiteTest(1, "Old", "Old");
        ManualTest test = new ManualTest(10, "T1", "");

        when(suiteDao.find(1L)).thenReturn(suite);
        when(testDao.find(10L)).thenReturn(test);

        ResponseEntity<String> resp = sut.updateSuite(1L, "New Name", "New Desc", List.of(10L));

        assertEquals(HttpStatus.SEE_OTHER, resp.getStatusCode());
        assertTrue(resp.getHeaders().getFirst("Location").contains("suiteDetails"),
                "La redirection doit contenir 'suiteDetails' en cas de mise à jour valide");
        verify(suiteDao).update(suite);
        assertEquals("New Name", suite.getName());
        assertEquals("New Desc", suite.getDescription());
        assertTrue(suite.getTests().contains(test), "Le test valide doit être ajouté à la suite");
    }

    @Test
    @DisplayName("POST /updateSuite: ne conserve que les tests trouvés parmi les testIds")
    public void testUpdateSuite_AddOnlyFoundTests() {
        // Prépare une suite avec un test ancien qui sera supprimé
        ManualSuiteTest suite = new ManualSuiteTest(1, "Suite", "Desc");
        ManualTest oldTest = new ManualTest(99, "OldTest", "OldDesc");
        suite.addTest(oldTest);

        // Prépare deux identifiants : pour l'un, le test est trouvé, pour l'autre, il
        // n'est pas trouvé
        ManualTest newTest = new ManualTest(2, "ValidTest", "Desc2");
        when(suiteDao.find(1L)).thenReturn(suite);
        when(testDao.find(2L)).thenReturn(newTest);
        when(testDao.find(3L)).thenReturn(null); // test non trouvé

        ResponseEntity<String> resp = sut.updateSuite(1L, "Updated Suite", "New Desc", List.of(2L, 3L));

        // Seul newTest doit être présent
        assertEquals(1, suite.getTests().size(), "Seul le test trouvé doit être ajouté à la suite");
        assertTrue(suite.getTests().contains(newTest), "La suite doit contenir le test trouvé");
        verify(suiteDao).update(suite);
        assertTrue(resp.getHeaders().getFirst("Location").contains("suiteDetails?id=1"),
                "La redirection doit pointer vers 'suiteDetails?id=1'");
    }

}
