package com.karpuk.account.emulator.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDbTransaction {

    private String type;
    private double amount;

}
