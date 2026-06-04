package com.payment_service.demo.dto;

import lombok.Data;

public class CreateOrderResponse {

    private String razorpayorderId;
    private String razorpaypaymentId;
    private String razorpaySignature;
}
