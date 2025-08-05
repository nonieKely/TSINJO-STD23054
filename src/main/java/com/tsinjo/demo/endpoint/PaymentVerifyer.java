package com.tsinjo.demo.endpoint;

import com.tsinjo.demo.models.PayementStatus;
import com.tsinjo.demo.models.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Service
public class PaymentVerifyer {

    @Value("${vola_api_key}")
    private String api_key;

    private static final Logger logger = LoggerFactory.getLogger(PaymentVerifyer.class);
    private static final String url = "https://42cwka3n4ifcp7ufheyrpmph240iuaxo.lambda-url.eu-west-3.on.aws/payment";
    private final RestTemplate restTemplate;

    public PaymentVerifyer(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Payment waitForFinalPaymentStatus(String payerEmail, String pspType, String pspPaymentId) throws InterruptedException {
        URI uri = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("apiKey", api_key) //
                .queryParam("payerEmail", payerEmail)
                .queryParam("pspType", pspType)
                .queryParam("pspPaymentId", pspPaymentId)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        int maxAttempt = 10;
        int delay = 5000;

        for (int i = 0; i < maxAttempt; i++) {
            ResponseEntity<Payment> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Payment.class);
            Payment payment = response.getBody();

            if (payment == null) {
                logger.warn("Paiement introuvable.");
                return null;
            }

            PayementStatus status = payment.getVerificationStatus();
            logger.info("Tentative {}: statut = {}", i + 1, status);

            if (status == PayementStatus.VERIFYING) {
                logger.info("Le paiement est toujours en cours de vérification...");
            } else if (status == PayementStatus.SUCCEEDED) {
                logger.info("Le paiement a réussi !");
                return payment;
            } else if (status == PayementStatus.FAILED) {
                logger.info("Le paiement a échoué.");
                return payment;
            } else {
                logger.warn("Statut inconnu : {}", status);
                return payment;
            }

            Thread.sleep(delay);
        }

        logger.warn("Timeout : le paiement n’a pas été vérifié après {} tentatives.", maxAttempt);
        return null;
    }
}
