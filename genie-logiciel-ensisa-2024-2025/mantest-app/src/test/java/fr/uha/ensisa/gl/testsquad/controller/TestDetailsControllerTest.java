package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.AfterEach;
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
import fr.uha.ensisa.gl.testsquad.mantest.ManualTest.Step;
import fr.uha.ensisa.gl.testsquad.mantest.ManualTestExecution;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;
import fr.uha.ensisa.gl.testsquad.mantest.status.StepStatus;

public class TestDetailsControllerTest {

        private TestDetailsController sut;
        private long idForTesting;

        @Mock
        private DaoFactory dao;

        @Mock
        private TestDao TestDaoMock;

        @Mock
        private SuiteTestDao SuiteDaoMock;

        @Mock
        private TestExecutionDao TestExecutionDaoMock;

        @Mock
        private ManualTest ManualTestMock;

        @BeforeEach
        public void setUp() {
                idForTesting = 1;
                MockitoAnnotations.openMocks(this);
                sut = new TestDetailsController();
                sut.setDao(dao);

                // Initialisation des mocks
                when(ManualTestMock.getId()).thenReturn(idForTesting);
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);
        }

        /**
         * After each test, verify only the interactions common to *all* tests:
         * (1) dao.getTestDao() and (2) TestDaoMock.find(idForTesting).
         *
         * We do NOT verify TestDaoMock.update(...) here because not all tests call it.
         * Instead, we verify update(...) in the specific tests that do.
         */

        @AfterEach
        public void verifyMockingInteractions() {
                // Vérifier que getTestDao() et find(id) ont été appelés
                verify(dao, atLeastOnce()).getTestDao();
                verify(TestDaoMock, atLeastOnce()).find(idForTesting);
                // Ne pas vérifier ici TestExecutionDaoMock si updateTest les utilise.
                //verifyNoMoreInteractions(TestDaoMock);
                //verifyNoMoreInteractions(TestExecutionDaoMock);
        }

        // --- Tests pour viewTestDetails ---

        @Test
        public void noTestFound() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(null);

                ModelAndView result = sut.viewTestDetails(idForTesting);
                Object not_found_test = result.getModel().get("errorMessage");

                assertInstanceOf(String.class, not_found_test);
                assertEquals("Test not found", not_found_test);
        }

        @Test
        public void testDetailsNotFound() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(null);

                ModelAndView result = sut.viewTestDetails(idForTesting);
                assertEquals("Test not found", result.getModelMap().get("errorMessage"));
        }

        @Test
        public void testViewDetailsEmptySteps() {
                ManualTest testInStore = new ManualTest(idForTesting);
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(testInStore);

                ModelAndView result = sut.viewTestDetails(idForTesting);
                assertEquals("Test not found", result.getModelMap().get("errorMessage"));
        }

        @Test
        public void testDetailsNotEmpty() {
                ManualTest testInStore = new ManualTest(idForTesting);
                String testStep1 = "This is the 1st test step.";
                String testStep2 = "This is the 2nd test step.";
                testInStore.addStep(testStep1);
                testInStore.addStep(testStep2);

                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(testInStore);

                ModelAndView result = sut.viewTestDetails(idForTesting);
                Object found_test = result.getModel().get("test");
                Object found_test_id = result.getModel().get("id");
                Object found_test_details = result.getModel().get("steps");

                assertInstanceOf(ManualTest.class, found_test);
                assertInstanceOf(Long.class, found_test_id);
                assertInstanceOf(List.class, found_test_details);
                assertFalse(((List<?>) found_test_details).isEmpty());
                assertEquals(2, ((List<?>) found_test_details).size());
                assertTrue(((Collection<?>) found_test_details)
                                .containsAll(List.of(new Step(testStep1), new Step(testStep2))));
        }

        // --- Tests pour deleteTest ---

        @Test
        public void deleteTestNotFound() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(null);

                ResponseEntity<String> response = sut.deleteTest(idForTesting);
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                // Notice we do *not* verify update() or remove() here, because remove() wasn't
                // called
                // (the test wasn't found).
        }

        @Test
        public void deleteTestFound() {
                ManualTest testInStore = new ManualTest(idForTesting);

                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);
                when(dao.getSuiteDao()).thenReturn(SuiteDaoMock); // <- ajout
                when(TestDaoMock.find(idForTesting)).thenReturn(testInStore);
                when(SuiteDaoMock.findAll()).thenReturn(List.of()); // <- ajout

                ResponseEntity<String> response = sut.deleteTest(idForTesting);

                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                assertEquals("./testList", Objects.requireNonNull(response.getHeaders().getLocation()).getPath());

                verify(TestDaoMock).remove(idForTesting);
                verify(TestExecutionDaoMock).removeByTestId(idForTesting);

                verify(dao).getTestExecutionDao();
                verify(dao).getSuiteDao(); // <- ajout
                verify(SuiteDaoMock).findAll(); // <- ajout
        }

        // --- Tests pour updateTest ---

        @Test
        public void updateTestNotFound() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(null);

                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "Test", "Description",
                                List.of("Step1", "Step2"));
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        }

        @Test
        public void updateTestFoundNullName() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, null, "NewDescription",
                                List.of("Step1", "Step2"));
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        }

        @Test
        public void updateTestFoundEmptyName() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "", "NewDescription",
                                List.of("Step1", "Step2"));
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        }

        @Test
        public void updateTestFoundNullDescription() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "Test1", null,
                                List.of("Step1", "Step2"));
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        }

        @Test
        public void updateTestFoundNullSteps() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "NewTestName", "NewDescription", null);
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        }

        @Test
        public void updateTestFoundEmptySteps() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "NewTestName", "NewDescription",
                                List.of());
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        }

        @Test
        public void updateTestFoundEmptyStepInSteps() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                // Une des étapes est vide, doit retourner SEE_OTHER
                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "NewTestName", "NewDescription",
                                List.of("", "NonEmptyStep"));

                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                verify(TestDaoMock, never()).update(any());
        }

        @Test
        public void updateTestFoundNullStepInSteps() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                // One step is null -> SEE_OTHER
                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "NewTestName", "NewDescription",
                                java.util.Arrays.asList(null, "NonEmptyStep"));
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
        }

        @Test
        public void updateTestFoundSteps() {
                // Stubber le DAO et le test
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                // S'assurer que le test retourne l'id attendu
                when(ManualTestMock.getId()).thenReturn(idForTesting);

                // Créer une exécution associée au test
                ManualTestExecution execution = new ManualTestExecution(idForTesting, ManualTestMock);

                // Stubber le DAO d'exécutions pour qu'il retourne une collection contenant
                // l'exécution créée
                when(dao.getTestExecutionDao()).thenReturn(TestExecutionDaoMock);
                when(TestExecutionDaoMock.findAll()).thenReturn(List.of(execution));

                // Appel de la méthode updateTest
                ResponseEntity<String> response = sut.updateTest(
                                idForTesting, "NewTestName", "NewDescription",
                                List.of("newStep1", "newStep2"));

                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

                // Vérifier que le test est bien mis à jour
                verify(ManualTestMock).setName("NewTestName");
                verify(ManualTestMock).setDescription("NewDescription");
                verify(ManualTestMock).removeAllSteps();
                // Puisque nous utilisons addSteps(...)
                verify(ManualTestMock).addSteps(List.of(
                                new Step("newStep1", StepStatus.UNDEFINED),
                                new Step("newStep2", StepStatus.UNDEFINED)));

                verify(TestDaoMock).update(ManualTestMock);
        }

        @Test
        public void updateTestStepsIsNull() {
                // Make sure the test object is found
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                // steps is null
                ResponseEntity<String> response = sut.updateTest(
                                idForTesting,
                                "SomeName",
                                "SomeDescription",
                                null);

                // We expect the code to return SEE_OTHER
                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                // Verify update(...) was never called
                verify(TestDaoMock, never()).update(any());
        }

        @Test
        public void updateTestStepsIsEmpty() {
                when(dao.getTestDao()).thenReturn(TestDaoMock);
                when(TestDaoMock.find(idForTesting)).thenReturn(ManualTestMock);

                // steps is empty (size=0)
                ResponseEntity<String> response = sut.updateTest(
                                idForTesting,
                                "SomeName",
                                "SomeDescription",
                                List.of() // steps.isEmpty() == true
                );

                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                verify(TestDaoMock, never()).update(any());
        }

        @Test
        public void deleteTest_SuiteDoesNotContainTest() {
                ManualTest testToDelete = new ManualTest(idForTesting);
                ManualTest unrelatedTest = new ManualTest(999L);

                ManualSuiteTest suite = new ManualSuiteTest();
                suite.addTest(unrelatedTest); // ne contient pas le test à supprimer

                when(TestDaoMock.find(idForTesting)).thenReturn(testToDelete);
                when(dao.getSuiteDao()).thenReturn(SuiteDaoMock);
                when(SuiteDaoMock.findAll()).thenReturn(List.of(suite));

                ResponseEntity<String> response = sut.deleteTest(idForTesting);

                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                assertTrue(response.getHeaders().getFirst("Location").contains("testList"));

                verify(TestDaoMock).remove(idForTesting);
                verify(TestExecutionDaoMock).removeByTestId(idForTesting);
                verify(SuiteDaoMock).findAll();
                verify(SuiteDaoMock, never()).update(suite);
                verify(SuiteDaoMock, never()).remove(suite.getId());
        }

        @Test
        public void deleteTest_SuiteStillHasOtherTests() {
                ManualTest testToDelete = new ManualTest(idForTesting);
                ManualTest otherTest = new ManualTest(999L);

                ManualSuiteTest suite = new ManualSuiteTest();
                suite.addTest(testToDelete);
                suite.addTest(otherTest); // la suite reste non vide

                when(TestDaoMock.find(idForTesting)).thenReturn(testToDelete);
                when(dao.getSuiteDao()).thenReturn(SuiteDaoMock);
                when(SuiteDaoMock.findAll()).thenReturn(List.of(suite));

                ResponseEntity<String> response = sut.deleteTest(idForTesting);

                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                assertTrue(response.getHeaders().getFirst("Location").contains("testList"));

                verify(TestDaoMock).remove(idForTesting);
                verify(TestExecutionDaoMock).removeByTestId(idForTesting);
                verify(SuiteDaoMock).findAll();
                verify(SuiteDaoMock).update(suite);
                verify(SuiteDaoMock, never()).remove(suite.getId());
        }

        @Test
        @DisplayName("DELETE /removeTest: le test supprimé est retiré de la suite")
        public void deleteTest_RemovesTestFromSuite() {
                ManualTest testToDelete = new ManualTest(idForTesting);
                ManualTest otherTest = new ManualTest(999L);
                ManualSuiteTest suite = new ManualSuiteTest();
                suite.addTest(testToDelete);
                suite.addTest(otherTest);

                when(TestDaoMock.find(idForTesting)).thenReturn(testToDelete);
                when(dao.getSuiteDao()).thenReturn(SuiteDaoMock);
                when(SuiteDaoMock.findAll()).thenReturn(List.of(suite));

                ResponseEntity<String> response = sut.deleteTest(idForTesting);

                assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());
                assertTrue(response.getHeaders().getFirst("Location").contains("testList"));
                assertFalse(suite.getTests().stream().anyMatch(t -> t.getId() == idForTesting));
                verify(SuiteDaoMock).update(suite);
        }

}
