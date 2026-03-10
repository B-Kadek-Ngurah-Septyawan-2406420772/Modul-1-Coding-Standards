package id.ac.ui.cs.advprog.eshop.functional;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class OrderFunctionalTest {

    @LocalServerPort
    private int serverPort;

    @Value("${app.baseUrl:http://localhost}")
    private String testBaseUrl;

    private String baseUrl;

    @BeforeEach
    void setupTest() {
        baseUrl = String.format("%s:%d", testBaseUrl, serverPort);
    }

    @Test
    void orderCreatePageLoads(ChromeDriver driver) {
        driver.get(baseUrl + "/order/create");

        assertEquals("Create Order", driver.getTitle());
        assertTrue(driver.findElement(By.id("authorInput")).isDisplayed());
        assertTrue(driver.findElement(By.id("productNameInput")).isDisplayed());
        assertTrue(driver.findElement(By.id("productQuantityInput")).isDisplayed());
    }

    @Test
    void createOrderThenSearchByAuthorShowsInHistory(ChromeDriver driver) {
        String author = "Author-" + System.currentTimeMillis();
        String productName = "Product-" + System.currentTimeMillis();

        driver.get(baseUrl + "/order/create");
        driver.findElement(By.id("authorInput")).sendKeys(author);
        driver.findElement(By.id("productNameInput")).sendKeys(productName);
        driver.findElement(By.id("productQuantityInput")).sendKeys("2");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.get(baseUrl + "/order/history");
        driver.findElement(By.id("historyAuthorInput")).sendKeys(author);
        driver.findElement(By.id("searchOrderButton")).click();

        WebElement resultAuthor = driver.findElement(By.id("searchedAuthor"));
        assertEquals(author, resultAuthor.getText());

        List<WebElement> rows = driver.findElements(By.cssSelector("#orderTable tbody tr"));
        assertTrue(rows.size() >= 1);
        String rowText = rows.get(0).getText();
        assertTrue(rowText.contains(author));
        assertTrue(rowText.contains("WAITING_PAYMENT"));
    }
}
