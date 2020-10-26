package com.karpuk.account.emulator.test.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "accounts")
public class TestDbAccount {

    @Id
    private String id;
    private String fullName;
    private LocalDate registrationDate = LocalDate.now();
    private List<TestDbTransaction> transactions;

    public TestDbAccount() {
    }

    public TestDbAccount(String id, String fullName, LocalDate registrationDate,
                         List<TestDbTransaction> transactions) {
        this.id = id;
        this.fullName = fullName;
        this.registrationDate = registrationDate;
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

    public List<TestDbTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TestDbTransaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return "TestDbAccount{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", registrationDate=" + registrationDate +
                ", transactions=" + transactions +
                '}';
    }

}
