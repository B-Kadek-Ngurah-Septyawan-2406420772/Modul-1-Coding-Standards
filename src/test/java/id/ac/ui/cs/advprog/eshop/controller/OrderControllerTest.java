package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.OrderRepository;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.service.OrderServiceImpl;
import id.ac.ui.cs.advprog.eshop.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderControllerTest {

    private OrderController orderController;
    private OrderServiceImpl orderService;
    private PaymentServiceImpl paymentService;
    private Order existingOrder;

    @BeforeEach
    void setUp() {
        OrderRepository orderRepository = new OrderRepository();
        PaymentRepository paymentRepository = new PaymentRepository();
        this.orderService = new OrderServiceImpl();
        this.paymentService = new PaymentServiceImpl(paymentRepository);

        // Injecting repository manually by reflection because OrderServiceImpl uses field injection.
        try {
            var field = OrderServiceImpl.class.getDeclaredField("orderRepository");
            field.setAccessible(true);
            field.set(orderService, orderRepository);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        this.orderController = new OrderController(orderService, paymentService);

        Product product = new Product();
        product.setProductId("product-1");
        product.setProductName("Sample Product");
        product.setProductQuantity(1);
        List<Product> products = new ArrayList<>();
        products.add(product);

        existingOrder = new Order("order-1", products, 1708560000L, "Safira");
        orderService.createOrder(existingOrder);
    }

    @Test
    void testCreateOrderPage() {
        String viewName = orderController.createOrderPage();
        assertEquals("orderCreate", viewName);
    }

    @Test
    void testCreateOrderPost() {
        String viewName = orderController.createOrderPost("Bambang", "Keyboard", 2);
        assertEquals("redirect:/order/history", viewName);
        assertEquals(1, orderService.findAllByAuthor("Bambang").size());
    }

    @Test
    void testOrderHistoryPage() {
        Model model = new ExtendedModelMap();
        String viewName = orderController.orderHistoryPage(model);

        assertEquals("orderHistory", viewName);
        assertEquals("", model.getAttribute("author"));
        assertInstanceOf(List.class, model.getAttribute("orders"));
    }

    @Test
    void testOrderHistoryPost() {
        Model model = new ExtendedModelMap();
        String viewName = orderController.orderHistoryPost("Safira", model);

        assertEquals("orderHistory", viewName);
        List<?> orders = (List<?>) model.getAttribute("orders");
        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    void testPayOrderPageIfFound() {
        Model model = new ExtendedModelMap();
        String viewName = orderController.payOrderPage(existingOrder.getId(), model);

        assertEquals("orderPay", viewName);
        assertEquals(existingOrder.getId(), ((Order) model.getAttribute("order")).getId());
    }

    @Test
    void testPayOrderPageIfNotFound() {
        String viewName = orderController.payOrderPage("missing-order", new ExtendedModelMap());
        assertEquals("redirect:/order/history", viewName);
    }

    @Test
    void testPayOrderPostVoucherCode() {
        Model model = new ExtendedModelMap();
        String viewName = orderController.payOrderPost(
                existingOrder.getId(),
                "VOUCHER_CODE",
                "ESHOP1234ABC5678",
                null,
                null,
                model
        );

        assertEquals("orderPaymentResult", viewName);
        Payment payment = (Payment) model.getAttribute("payment");
        assertNotNull(payment);
        assertEquals(Payment.STATUS_SUCCESS, payment.getStatus());
    }

    @Test
    void testPayOrderPostBankTransfer() {
        Model model = new ExtendedModelMap();
        String viewName = orderController.payOrderPost(
                existingOrder.getId(),
                "BANK_TRANSFER",
                null,
                "BCA",
                "INV-001",
                model
        );

        assertEquals("orderPaymentResult", viewName);
        Payment payment = (Payment) model.getAttribute("payment");
        assertNotNull(payment);
        assertEquals(Payment.STATUS_PENDING, payment.getStatus());
    }

    @Test
    void testPayOrderPostIfNotFound() {
        String viewName = orderController.payOrderPost(
                "missing-order",
                "VOUCHER_CODE",
                "ESHOP1234ABC5678",
                null,
                null,
                new ExtendedModelMap()
        );
        assertEquals("redirect:/order/history", viewName);
    }

    @Test
    void testPayOrderPostWithUnsupportedMethod() {
        Model model = new ExtendedModelMap();
        String viewName = orderController.payOrderPost(
                existingOrder.getId(),
                "UNSUPPORTED_METHOD",
                null,
                null,
                null,
                model
        );

        assertEquals("orderPaymentResult", viewName);
        Payment payment = (Payment) model.getAttribute("payment");
        assertNotNull(payment);
        assertEquals(Payment.STATUS_PENDING, payment.getStatus());
    }
}
