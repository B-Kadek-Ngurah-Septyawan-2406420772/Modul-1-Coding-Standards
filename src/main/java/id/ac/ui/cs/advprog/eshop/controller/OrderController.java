package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final String VOUCHER_CODE_METHOD = "VOUCHER_CODE";
    private static final String BANK_TRANSFER_METHOD = "BANK_TRANSFER";

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @GetMapping("/create")
    public String createOrderPage() {
        return "orderCreate";
    }

    @PostMapping("/create")
    public String createOrderPost(
            @RequestParam String author,
            @RequestParam String productName,
            @RequestParam int productQuantity
    ) {
        Product product = new Product();
        product.setProductId(UUID.randomUUID().toString());
        product.setProductName(productName);
        product.setProductQuantity(productQuantity);

        List<Product> products = new ArrayList<>();
        products.add(product);

        Order order = new Order(
                UUID.randomUUID().toString(),
                products,
                System.currentTimeMillis(),
                author
        );
        orderService.createOrder(order);
        return "redirect:/order/history";
    }

    @GetMapping("/history")
    public String orderHistoryPage(Model model) {
        model.addAttribute("author", "");
        model.addAttribute("orders", new ArrayList<Order>());
        return "orderHistory";
    }

    @PostMapping("/history")
    public String orderHistoryPost(@RequestParam String author, Model model) {
        List<Order> orders = orderService.findAllByAuthor(author);
        model.addAttribute("author", author);
        model.addAttribute("orders", orders);
        return "orderHistory";
    }

    @GetMapping("/pay/{orderId}")
    public String payOrderPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }
        model.addAttribute("order", order);
        return "orderPay";
    }

    @PostMapping("/pay/{orderId}")
    public String payOrderPost(
            @PathVariable String orderId,
            @RequestParam String method,
            @RequestParam(required = false) String voucherCode,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String referenceCode,
            Model model
    ) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }

        Map<String, String> paymentData = new HashMap<>();
        if (VOUCHER_CODE_METHOD.equals(method)) {
            paymentData.put("voucherCode", voucherCode);
        } else if (BANK_TRANSFER_METHOD.equals(method)) {
            paymentData.put("bankName", bankName);
            paymentData.put("referenceCode", referenceCode);
        }

        Payment payment = paymentService.addPayment(order, method, paymentData);
        model.addAttribute("order", order);
        model.addAttribute("payment", payment);
        return "orderPaymentResult";
    }
}
