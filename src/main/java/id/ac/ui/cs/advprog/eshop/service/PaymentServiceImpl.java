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
    private static final String BANK_TRANSFER_METHOD = "BANK_TRANSFER";
    private static final String VOUCHER_CODE_KEY = "voucherCode";
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REFERENCE_CODE_KEY = "referenceCode";
    private static final String VOUCHER_PREFIX = "ESHOP";
    private static final int VOUCHER_LENGTH = 16;
    private static final int VOUCHER_REQUIRED_DIGIT_COUNT = 8;

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        Payment payment = new Payment(UUID.randomUUID().toString(), order, method, paymentData);
        applyInitialStatus(payment);
        return paymentRepository.save(payment);
    }

    private void applyInitialStatus(Payment payment) {
        if (VOUCHER_CODE_METHOD.equals(payment.getMethod())) {
            setVoucherCodeStatus(payment);
            return;
        }
        if (BANK_TRANSFER_METHOD.equals(payment.getMethod())) {
            setBankTransferStatus(payment);
        }
    }

    private void setVoucherCodeStatus(Payment payment) {
        String voucherCode = payment.getPaymentData().get(VOUCHER_CODE_KEY);
        if (isValidVoucherCode(voucherCode)) {
            payment.setStatus(Payment.STATUS_SUCCESS);
        } else {
            payment.setStatus(Payment.STATUS_REJECTED);
        }
    }

    private void setBankTransferStatus(Payment payment) {
        String bankName = payment.getPaymentData().get(BANK_NAME_KEY);
        String referenceCode = payment.getPaymentData().get(REFERENCE_CODE_KEY);
        if (!hasText(bankName) || !hasText(referenceCode)) {
            payment.setStatus(Payment.STATUS_REJECTED);
        }
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
        if (voucherCode == null || voucherCode.length() != VOUCHER_LENGTH) {
            return false;
        }
        if (!voucherCode.startsWith(VOUCHER_PREFIX)) {
            return false;
        }
        return countDigits(voucherCode) == VOUCHER_REQUIRED_DIGIT_COUNT;
    }

    private int countDigits(String value) {
        int digitCount = 0;
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                digitCount += 1;
            }
        }
        return digitCount;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
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
