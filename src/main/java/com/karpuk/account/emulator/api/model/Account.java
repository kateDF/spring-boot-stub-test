package com.karpuk.account.emulator.api.model;

import java.time.LocalDate;
import java.util.List;

public class Account {

    private Long id;
    private String fullName;
    private LocalDate registrationDate;
    private Balance balance;
    private List<Transaction> transactions;

    public Account(Long id, String fullName, List<Transaction> transactions) {
        this.id = id;
        this.fullName = fullName;
        this.transactions = transactions;
        this.registrationDate = LocalDate.now();
        this.balance = new Balance(transactions);
    }

    public Account(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.registrationDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public Balance getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (fullName != null ? !fullName.equals(account.fullName) : account.fullName != null) return false;
        if (registrationDate != null ? !registrationDate.equals(account.registrationDate) : account.registrationDate != null)
            return false;
        if (balance != null ? !balance.equals(account.balance) : account.balance != null) return false;
        return transactions != null ? transactions.equals(account.transactions) : account.transactions == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (transactions != null ? transactions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", registrationDate=" + registrationDate +
                ", balance=" + balance +
                ", transactions=" + transactions +
                '}';
    }

}
