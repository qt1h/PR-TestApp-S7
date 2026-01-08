package fr.uha.ensisa.gl.testsquad.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import fr.uha.ensisa.gl.testsquad.mantest.dao.SuiteTestDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.uha.ensisa.gl.testsquad.mantest.dao.DaoFactory;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestDao;
import fr.uha.ensisa.gl.testsquad.mantest.dao.TestExecutionDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

public class AdminControllerTest {

    private AdminController sut;

    @Mock
    private DaoFactory dao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new AdminController();
        sut.setDao(dao);
    }

    @AfterEach
    public void verifyMockingInteractions() {
        verifyNoMoreInteractions(dao);
    }

    @Test
    public void resetDao() {
        ResponseEntity<String> response = sut.reset();

        assertEquals(HttpStatus.SEE_OTHER, response.getStatusCode());

        verify(dao).reset();
    }

    @Test
    public void admin() {
        AdminController sut = new AdminController();
        ModelAndView result = sut.admin();
        assertNotNull(result);
        assertEquals("admin", result.getViewName());
    }

}
