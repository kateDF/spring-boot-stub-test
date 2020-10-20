package com.karpuk.account.emulator.upstream.currency.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RateValues {

    @JsonProperty("EUR")
    private double eur;

    public RateValues() {
    }

    public double getEur() {
        return eur;
    }

    public void setEur(double eur) {
        this.eur = eur;
    }

}
