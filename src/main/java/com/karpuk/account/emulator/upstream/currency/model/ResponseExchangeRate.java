package com.karpuk.account.emulator.upstream.currency.model;

import java.time.LocalDate;

public class ResponseExchangeRate {

    private String base;
    private LocalDate date;
    private RateValues rates;

    public ResponseExchangeRate() {
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public RateValues getRates() {
        return rates;
    }

    public void setRates(RateValues rates) {
        this.rates = rates;
    }

}
