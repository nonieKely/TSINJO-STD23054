package com.tsinjo.demo.endpoint.rest.controller;

import com.tsinjo.demo.endpoint.PaymentVerifyer;
import com.tsinjo.demo.models.Payment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DonationController {

    private final PaymentVerifyer paymentVerifyer;

    public DonationController(PaymentVerifyer paymentVerifyer) {
        this.paymentVerifyer = paymentVerifyer;
    }

    @GetMapping("/donate")
    public String showForm() {
        return "donation";
    }

    @PostMapping("/donate")
    public String submitForm(
            @RequestParam String email,
            @RequestParam String paymentMethod,
            @RequestParam String paymentReference,
            Model model) throws InterruptedException {

        Payment payment = paymentVerifyer.waitForFinalPaymentStatus(email, paymentMethod, paymentReference);

        if (payment == null) {
            model.addAttribute("message", "Erreur : paiement introuvable.");
        } else if (payment.getVerificationStatus().toString().equals("SUCCEEDED")) {
            model.addAttribute("message", "Merci ! Votre paiement a été validé.");
        } else {
            model.addAttribute("message", "Le paiement a échoué ou est en attente.");
        }

        return "donation";
    }
}
