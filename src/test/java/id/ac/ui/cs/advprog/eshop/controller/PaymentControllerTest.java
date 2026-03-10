package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentControllerTest {

    private PaymentController paymentController;
    private PaymentServiceImpl paymentService;
    private Payment existingPayment;

    @BeforeEach
    void setUp() {
        PaymentRepository paymentRepository = new PaymentRepository();
        this.paymentService = new PaymentServiceImpl(paymentRepository);
        this.paymentController = new PaymentController(paymentService);

        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sample Product");
        product.setProductQuantity(1);
        List<Product> products = new ArrayList<>();
        products.add(product);

        Order order = new Order("order-1", products, 1708560000L, "Safira");
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        existingPayment = paymentService.addPayment(order, "VOUCHER_CODE", paymentData);
    }

    @Test
    void testPaymentDetailFormPage() {
        String viewName = paymentController.paymentDetailFormPage();
        assertEquals("paymentDetailForm", viewName);
    }

    @Test
    void testPaymentDetailPageIfFound() {
        Model model = new ExtendedModelMap();
        String viewName = paymentController.paymentDetailPage(existingPayment.getId(), model);

        assertEquals("paymentDetail", viewName);
        Payment payment = (Payment) model.getAttribute("payment");
        assertNotNull(payment);
        assertEquals(existingPayment.getId(), payment.getId());
    }

    @Test
    void testPaymentDetailPageIfNotFound() {
        Model model = new ExtendedModelMap();
        String viewName = paymentController.paymentDetailPage("missing-payment", model);

        assertEquals("paymentDetail", viewName);
        assertNull(model.getAttribute("payment"));
    }

    @Test
    void testPaymentAdminListPage() {
        Model model = new ExtendedModelMap();
        String viewName = paymentController.paymentAdminListPage(model);

        assertEquals("paymentAdminList", viewName);
        List<?> payments = (List<?>) model.getAttribute("payments");
        assertNotNull(payments);
        assertEquals(1, payments.size());
    }

    @Test
    void testPaymentAdminDetailPageIfFound() {
        Model model = new ExtendedModelMap();
        String viewName = paymentController.paymentAdminDetailPage(existingPayment.getId(), model);

        assertEquals("paymentAdminDetail", viewName);
        Payment payment = (Payment) model.getAttribute("payment");
        assertNotNull(payment);
        assertEquals(existingPayment.getId(), payment.getId());
    }

    @Test
    void testPaymentAdminDetailPageIfNotFound() {
        Model model = new ExtendedModelMap();
        String viewName = paymentController.paymentAdminDetailPage("missing-payment", model);

        assertEquals("paymentAdminDetail", viewName);
        assertNull(model.getAttribute("payment"));
    }

    @Test
    void testPaymentAdminSetStatusPostIfFound() {
        String viewName = paymentController.paymentAdminSetStatusPost(existingPayment.getId(), Payment.STATUS_REJECTED);

        assertEquals("redirect:/payment/admin/detail/" + existingPayment.getId(), viewName);
        Payment updated = paymentService.getPayment(existingPayment.getId());
        assertNotNull(updated);
        assertEquals(Payment.STATUS_REJECTED, updated.getStatus());
    }

    @Test
    void testPaymentAdminSetStatusPostIfNotFound() {
        String viewName = paymentController.paymentAdminSetStatusPost("missing-payment", Payment.STATUS_SUCCESS);
        assertEquals("redirect:/payment/admin/list", viewName);
    }
}
