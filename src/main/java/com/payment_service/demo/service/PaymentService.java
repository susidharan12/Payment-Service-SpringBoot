package com.payment_service.demo.service;

import com.payment_service.demo.dto.CreateOrderRequest;
import com.payment_service.demo.dto.VerifyPaymentRequest;
import com.payment_service.demo.model.Payment;
import com.payment_service.demo.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final PaymentRepository paymentRepository;
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    public List<Payment> getSuccessfulPayments() {
        return paymentRepository.findByStatus("SUCCESS");
    }

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Map<String, Object> createOrder(CreateOrderRequest request) throws Exception {

        RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", request.getAmount());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setRazorpayOrderId(order.get("id"));
        payment.setAmount(request.getAmount());
        payment.setCurrency("INR");
        payment.setStatus("CREATED");

        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.get("id"));
        response.put("amount", request.getAmount());
        response.put("currency", "INR");
        response.put("key", keyId);

        return response;
    }

    public String verifyPayment(VerifyPaymentRequest request) throws Exception {

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", request.getRazorpayOrderId());
        options.put("razorpay_payment_id", request.getRazorpayPaymentId());
        options.put("razorpay_signature", request.getRazorpaySignature());

        boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

        Payment payment = paymentRepository
                .findByRazorpayOrderId(request.getRazorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (isValid) {
            payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);
            return "Payment verified successfully";
        } else {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);
            return "Payment verification failed";
        }
    }
}