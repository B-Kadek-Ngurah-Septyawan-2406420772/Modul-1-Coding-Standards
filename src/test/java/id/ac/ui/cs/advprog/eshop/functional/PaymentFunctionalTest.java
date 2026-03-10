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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SeleniumJupiter.class)
class PaymentFunctionalTest {

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
    void payOrderAndManageStatusFromAdminFlowWorks(ChromeDriver driver) {
        String author = "PaymentAuthor-" + System.currentTimeMillis();

        driver.get(baseUrl + "/order/create");
        driver.findElement(By.id("authorInput")).sendKeys(author);
        driver.findElement(By.id("productNameInput")).sendKeys("Voucher Product");
        driver.findElement(By.id("productQuantityInput")).sendKeys("1");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.get(baseUrl + "/order/history");
        driver.findElement(By.id("historyAuthorInput")).sendKeys(author);
        driver.findElement(By.id("searchOrderButton")).click();

        WebElement payButton = driver.findElement(By.cssSelector("#orderTable tbody tr td a"));
        payButton.click();

        WebElement methodSelect = driver.findElement(By.id("methodSelect"));
        methodSelect.sendKeys("VOUCHER_CODE");
        driver.findElement(By.id("voucherCodeInput")).sendKeys("ESHOP1234ABC5678");
        driver.findElement(By.id("payOrderButton")).click();

        String paymentId = driver.findElement(By.id("paymentIdValue")).getText();
        assertFalse(paymentId.isBlank());

        driver.get(baseUrl + "/payment/detail/" + paymentId);
        assertEquals(paymentId, driver.findElement(By.id("paymentDetailId")).getText());
        assertEquals("SUCCESS", driver.findElement(By.id("paymentDetailStatus")).getText());

        driver.get(baseUrl + "/payment/admin/list");
        List<WebElement> rows = driver.findElements(By.cssSelector("#paymentTable tbody tr"));
        assertTrue(rows.size() >= 1);

        driver.findElement(By.cssSelector("#paymentTable tbody tr:first-child .adminDetailButton")).click();
        WebElement statusSelect = driver.findElement(By.id("statusSelect"));
        statusSelect.sendKeys("REJECTED");
        driver.findElement(By.id("setStatusButton")).click();

        assertEquals("REJECTED", driver.findElement(By.id("adminPaymentStatus")).getText());
    }
}
