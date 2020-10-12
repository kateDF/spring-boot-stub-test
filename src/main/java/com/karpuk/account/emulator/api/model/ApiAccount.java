package com.karpuk.account.emulator.api.model;

import java.time.LocalDate;
import java.util.List;

public class ApiAccount {

    private Long id;
    private String fullName;
    private LocalDate registrationDate = LocalDate.now();
    private ApiBalance apiBalance;
    private List<ApiTransaction> apiTransactions;

    public ApiAccount() {
    }

    public ApiAccount(Long id, String fullName, LocalDate registrationDate, ApiBalance apiBalance, List<ApiTransaction> apiTransactions) {
        this.id = id;
        this.fullName = fullName;
        this.registrationDate = registrationDate;
        this.apiBalance = apiBalance;
        this.apiTransactions = apiTransactions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ApiBalance getApiBalance() {
        return apiBalance;
    }

    public void setApiBalance(ApiBalance apiBalance) {
        this.apiBalance = apiBalance;
    }

    public List<ApiTransaction> getApiTransactions() {
        return apiTransactions;
    }

    public void setApiTransactions(List<ApiTransaction> apiTransactions) {
        this.apiTransactions = apiTransactions;
    }
}
