package com.tsinjo.demo.endpoint.rest.controller;

import com.tsinjo.demo.endpoint.PaymentVerifyer;
import com.tsinjo.demo.models.Payment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DonationVerificationController {

    private final PaymentVerifyer payementVerifyer;

    public DonationVerificationController(PaymentVerifyer payementVerifyer) {
        this.payementVerifyer = payementVerifyer;
    }

    @GetMapping("/api/payment/check")
    public Payment checkPayment(
            @RequestParam String payerEmail,
            @RequestParam String pspType,
            @RequestParam String pspPaymentId,
            @RequestHeader("X-API-KEY") String apiKeyHeader) throws InterruptedException {

        return payementVerifyer.waitForFinalPaymentStatus(payerEmail, pspType, pspPaymentId);
    }
}
