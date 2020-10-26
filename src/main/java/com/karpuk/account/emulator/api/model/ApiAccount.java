package com.karpuk.account.emulator.api.model;

import java.time.LocalDate;
import java.util.List;

public class ApiAccount {

    private String id;
    private String fullName;
    private LocalDate registrationDate = LocalDate.now();
    private ApiBalance balance;
    private List<ApiTransaction> transactions;

    public ApiAccount() {
    }

    public ApiAccount(String id, String fullName, LocalDate registrationDate, ApiBalance balance, List<ApiTransaction> transactions) {
        this.id = id;
        this.fullName = fullName;
        this.registrationDate = registrationDate;
        this.balance = balance;
        this.transactions = transactions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public ApiBalance getBalance() {
        return balance;
    }

    public void setBalance(ApiBalance balance) {
        this.balance = balance;
    }

    public List<ApiTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<ApiTransaction> transactions) {
        this.transactions = transactions;
    }
}
