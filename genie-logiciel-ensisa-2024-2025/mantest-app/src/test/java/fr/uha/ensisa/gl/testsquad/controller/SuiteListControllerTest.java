package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import fr.uha.ensisa.gl.testsquad.mantest.ManualSuiteTest;
import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;

public class SuiteListControllerTest {

    private SuiteListController sut;

    @Mock
    private DaoFactory dao;

    @Mock
    private SuiteTestDao suiteDaoMock;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new SuiteListController();
        sut.setDao(dao);
    }

    @Test
    public void testListSuites() {
        ManualSuiteTest suite1 = new ManualSuiteTest(1, "Suite 1", "Description 1");
        ManualSuiteTest suite2 = new ManualSuiteTest(2, "Suite 2", "Description 2");
        Collection<ManualSuiteTest> suites = Arrays.asList(suite1, suite2);
        when(dao.getSuiteDao()).thenReturn(suiteDaoMock);
        when(suiteDaoMock.findAll()).thenReturn(suites);

        ModelAndView mav = sut.listSuites();
        assertNotNull(mav);
        assertEquals("suiteList", mav.getViewName());
        assertEquals(suites, mav.getModel().get("suites"));
    }
}
