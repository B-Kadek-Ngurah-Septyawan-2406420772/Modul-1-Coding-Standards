package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    @Mock
    PaymentRepository paymentRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        products.add(product);

        order = new Order(
                "13652556-012a-4c07-b546-54eb1396d79b",
                products,
                1708560000L,
                "Safira Sudrajat"
        );
    }

    @Test
    void testAddPaymentSavesPaymentToRepository() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "INV-001");

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Payment result = paymentService.addPayment(order, "BANK_TRANSFER", paymentData);

        assertNotNull(result.getId());
        assertEquals("BANK_TRANSFER", result.getMethod());
        assertEquals("INV-001", result.getPaymentData().get("referenceCode"));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testSetStatusToSuccessAlsoUpdatesOrderStatus() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        Payment payment = new Payment("payment-1", order, "VOUCHER_CODE", paymentData);
        doReturn(payment).when(paymentRepository).save(payment);

        Payment result = paymentService.setStatus(payment, Payment.STATUS_SUCCESS);

        assertEquals(Payment.STATUS_SUCCESS, result.getStatus());
        assertEquals(OrderStatus.SUCCESS.getValue(), result.getOrder().getStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testSetStatusToRejectedAlsoUpdatesOrderStatus() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");

        Payment payment = new Payment("payment-1", order, "VOUCHER_CODE", paymentData);
        doReturn(payment).when(paymentRepository).save(payment);

        Payment result = paymentService.setStatus(payment, Payment.STATUS_REJECTED);

        assertEquals(Payment.STATUS_REJECTED, result.getStatus());
        assertEquals(OrderStatus.FAILED.getValue(), result.getOrder().getStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testGetPayment() {
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("voucherCode", "ESHOP1234ABC5678");
        Payment payment = new Payment("payment-1", order, "VOUCHER_CODE", paymentData);

        doReturn(payment).when(paymentRepository).findById("payment-1");

        Payment result = paymentService.getPayment("payment-1");
        assertEquals("payment-1", result.getId());
    }

    @Test
    void testGetAllPayments() {
        Map<String, String> firstData = new HashMap<>();
        firstData.put("voucherCode", "ESHOP1234ABC5678");
        Payment firstPayment = new Payment("payment-1", order, "VOUCHER_CODE", firstData);

        Map<String, String> secondData = new HashMap<>();
        secondData.put("bankName", "BCA");
        secondData.put("referenceCode", "INV-001");
        Payment secondPayment = new Payment("payment-2", order, "BANK_TRANSFER", secondData);

        List<Payment> payments = new ArrayList<>();
        payments.add(firstPayment);
        payments.add(secondPayment);
        doReturn(payments).when(paymentRepository).findAll();

        List<Payment> results = paymentService.getAllPayments();
        assertEquals(2, results.size());
    }
}
