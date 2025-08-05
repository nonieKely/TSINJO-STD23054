package com.tsinjo.demo.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
    private String pspPaymentId;
    private Integer amount;
    private PspType type;
    private String creationInstant; //datetime
    private PayementStatus verificationStatus;
}
