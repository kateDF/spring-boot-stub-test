package com.karpuk.account.emulator.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccount {

    private String id;
    private String fullName;
    private LocalDate registrationDate = LocalDate.now();
    private ApiBalance balance;
    private List<ApiTransaction> transactions;

}
