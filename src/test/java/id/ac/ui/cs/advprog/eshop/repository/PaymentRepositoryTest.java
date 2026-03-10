package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;
    private List<Payment> payments;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        product.setProductName("Sampo Cap Bambang");
        product.setProductQuantity(2);
        products.add(product);

        Order order = new Order(
                "13652556-012a-4c07-b546-54eb1396d79b",
                products,
                1708560000L,
                "Safira Sudrajat"
        );

        Map<String, String> voucherData = new HashMap<>();
        voucherData.put("voucherCode", "ESHOP1234ABC5678");

        payments = new ArrayList<>();
        Payment payment1 = new Payment("payment-1", order, "VOUCHER_CODE", voucherData);
        payments.add(payment1);

        Map<String, String> transferData = new HashMap<>();
        transferData.put("bankName", "BCA");
        transferData.put("referenceCode", "INV-001");
        Payment payment2 = new Payment("payment-2", order, "BANK_TRANSFER", transferData);
        payments.add(payment2);
    }

    @Test
    void testSaveCreate() {
        Payment payment = payments.get(0);
        Payment result = paymentRepository.save(payment);

        Payment findResult = paymentRepository.findById(payment.getId());
        assertEquals(payment.getId(), result.getId());
        assertEquals(payment.getMethod(), findResult.getMethod());
        assertEquals(payment.getStatus(), findResult.getStatus());
    }

    @Test
    void testSaveUpdate() {
        Payment payment = payments.get(0);
        paymentRepository.save(payment);

        Payment updatedPayment = new Payment(payment.getId(), payment.getOrder(), payment.getMethod(), payment.getPaymentData());
        updatedPayment.setStatus(Payment.STATUS_SUCCESS);

        Payment result = paymentRepository.save(updatedPayment);

        Payment findResult = paymentRepository.findById(payment.getId());
        assertEquals(payment.getId(), result.getId());
        assertEquals(Payment.STATUS_SUCCESS, findResult.getStatus());
    }

    @Test
    void testFindByIdIfIdFound() {
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }

        Payment findResult = paymentRepository.findById(payments.get(1).getId());
        assertEquals(payments.get(1).getId(), findResult.getId());
        assertEquals(payments.get(1).getMethod(), findResult.getMethod());
    }

    @Test
    void testFindByIdIfIdNotFound() {
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }

        Payment findResult = paymentRepository.findById("unknown-id");
        assertNull(findResult);
    }

    @Test
    void testFindByIdIfIdIsNull() {
        Payment findResult = paymentRepository.findById(null);
        assertNull(findResult);
    }

    @Test
    void testFindAllReturnsAllPayments() {
        for (Payment payment : payments) {
            paymentRepository.save(payment);
        }

        List<Payment> allPayments = paymentRepository.findAll();
        assertEquals(2, allPayments.size());
    }
}
