package id.ac.ui.cs.advprog.eshop.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Payment {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_REJECTED = "REJECTED";

    private final String id;
    private final Order order;
    private final String method;
    private final Map<String, String> paymentData;
    private String status;

    public Payment(String id, Order order, String method, Map<String, String> paymentData) {
        this.id = id;
        this.order = order;
        this.method = method;
        this.paymentData = paymentData == null ? new HashMap<>() : new HashMap<>(paymentData);
        this.status = STATUS_PENDING;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
