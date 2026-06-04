package com.payment_service.demo.controller;

import com.payment_service.demo.dto.CreateOrderRequest;
import com.payment_service.demo.dto.VerifyPaymentRequest;
import com.payment_service.demo.model.Payment;
import com.payment_service.demo.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin("*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @GetMapping("/test")
    public String test() {
        return "Payment Service Running";
    }
    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @PostMapping("/create-order")
    public Map<String, Object> createOrder(@RequestBody CreateOrderRequest request) throws Exception {
        return paymentService.createOrder(request);
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestBody VerifyPaymentRequest request) throws Exception {
        return paymentService.verifyPayment(request);
    }


}