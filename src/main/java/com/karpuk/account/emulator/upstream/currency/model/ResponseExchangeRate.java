package com.karpuk.account.emulator.upstream.currency.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseExchangeRate {

    private String base;
    private LocalDate date;
    private RateValues rates;

}
