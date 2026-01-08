package fr.uha.ensisa.gl.testsquad.it;

import org.junit.jupiter.api.Test;

public class ErrorsIT extends BaseIT {

    @Test
    public void test400Error() {
        navigateAndAssertTitle("400", "400", 10);
    }

    @Test
    public void test403Error() {
        navigateAndAssertTitle("403", "403", 10);
    }

    @Test
    public void test404Error() {
        navigateAndAssertTitle("404", "404", 10);
    }

    @Test
    public void test405Error() {
        navigateAndAssertTitle("405", "405", 10);
    }

    @Test
    public void test500Error() {
        navigateAndAssertTitle("500", "500", 10);
    }

    @Test
    public void nonexistentPageSend404Error() {
        navigateAndAssertTitle("qdfqfsghgfqsh", "404", 10);
    }
}
