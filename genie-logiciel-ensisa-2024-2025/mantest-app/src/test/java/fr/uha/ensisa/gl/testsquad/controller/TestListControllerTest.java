package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;


public class TestListControllerTest {

    private TestListController sut;

    @Mock
    private DaoFactory dao;

    @Mock
    private TestDao TestDaoMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new TestListController();
        sut.setDao(dao);
    }

    @AfterEach
    public void verifyMockingInteractions() {
        verify(TestDaoMock).findAll();
        verifyNoMoreInteractions(TestDaoMock);
    }

    @Test
    public void listNoTest() {
        when(dao.getTestDao()).thenReturn(TestDaoMock);

        ModelAndView result = sut.listTests();
        Object found_tests = result.getModel().get("tests");

        assertInstanceOf(Collection.class, found_tests);
        assertTrue(((Collection<?>) found_tests).isEmpty());
    }

    @Test
    public void listSomeTests() {
        ManualTest testInStore = new ManualTest();
        ManualTest testInStore2 = new ManualTest();
        ManualTest testInStore3 = new ManualTest();

        when(dao.getTestDao()).thenReturn(TestDaoMock);
        when(TestDaoMock.findAll()).thenReturn(List.of(testInStore, testInStore2, testInStore3));

        ModelAndView result = sut.listTests();
        Object found_tests = result.getModel().get("tests");

        assertInstanceOf(Collection.class, found_tests);
        assertFalse(((Collection<?>) found_tests).isEmpty());
        assertEquals(3, ((Collection<?>) found_tests).size());
        assertTrue(((Collection<?>) found_tests).containsAll(List.of(testInStore, testInStore2, testInStore3)));
    }
}
