package id.ac.ui.cs.advprog.eshop.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HomePageControllerTest {

    @Test
    void testHomePage() {
        HomePageController controller = new HomePageController();

        String viewName = controller.homePage();

        assertEquals("HomePage", viewName);
    }
}
