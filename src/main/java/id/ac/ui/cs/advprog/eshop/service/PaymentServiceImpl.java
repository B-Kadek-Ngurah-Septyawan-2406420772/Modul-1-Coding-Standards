package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String VOUCHER_CODE_METHOD = "VOUCHER_CODE";

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment(UUID.randomUUID().toString(), order, method, paymentData);
        if (VOUCHER_CODE_METHOD.equals(method)) {
            String voucherCode = payment.getPaymentData().get("voucherCode");
            if (isValidVoucherCode(voucherCode)) {
                payment.setStatus(Payment.STATUS_SUCCESS);
            } else {
                payment.setStatus(Payment.STATUS_REJECTED);
            }
        }
        return paymentRepository.save(payment);
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        updateRelatedOrderStatus(payment, status);
        return paymentRepository.save(payment);
    }

    private void updateRelatedOrderStatus(Payment payment, String status) {
        if (Payment.STATUS_SUCCESS.equals(status)) {
            payment.getOrder().setStatus(OrderStatus.SUCCESS.getValue());
        } else if (Payment.STATUS_REJECTED.equals(status)) {
            payment.getOrder().setStatus(OrderStatus.FAILED.getValue());
        }
    }

    private boolean isValidVoucherCode(String voucherCode) {
        if (voucherCode == null || voucherCode.length() != 16) {
            return false;
        }
        if (!voucherCode.startsWith("ESHOP")) {
            return false;
        }

        int digitCount = 0;
        for (int i = 0; i < voucherCode.length(); i++) {
            if (Character.isDigit(voucherCode.charAt(i))) {
                digitCount += 1;
            }
        }
        return digitCount == 8;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
