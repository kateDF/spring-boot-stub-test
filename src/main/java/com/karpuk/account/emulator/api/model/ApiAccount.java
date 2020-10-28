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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiAccount that = (ApiAccount) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (fullName != null ? !fullName.equals(that.fullName) : that.fullName != null) return false;
        if (registrationDate != null ? !registrationDate.equals(that.registrationDate) : that.registrationDate != null)
            return false;
        if (balance != null ? !balance.equals(that.balance) : that.balance != null) return false;
        return transactions != null ? transactions.equals(that.transactions) : that.transactions == null;
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
        return "ApiAccount{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", registrationDate=" + registrationDate +
                ", balance=" + balance +
                ", transactions=" + transactions +
                '}';
    }

}
