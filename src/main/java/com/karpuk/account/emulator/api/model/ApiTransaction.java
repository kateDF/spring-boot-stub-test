package com.karpuk.account.emulator.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiTransaction {

    private String type;
    private double amount;

}
