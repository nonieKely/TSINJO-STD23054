package com.tsinjo.demo.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Help {
    public Beneficiary beneficiary;
    public Donation details;
    public String incidentDescription;
}
