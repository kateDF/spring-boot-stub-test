package com.karpuk.account.emulator.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DbTransaction {

    private String type;
    private double amount;

}
