package com.karpuk.account.emulator.db.model;

import java.time.LocalDate;
import java.util.List;

public class DbAccount {

    private Long id;
    private String fullName;
    private LocalDate registrationDate = LocalDate.now();
    private List<DbTransaction> transactions;

    public DbAccount() {
    }

    public DbAccount(Long id, String fullName, LocalDate registrationDate, List<DbTransaction> transactions) {
        this.id = id;
        this.fullName = fullName;
        this.registrationDate = registrationDate;
        this.transactions = transactions;
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

    public List<DbTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<DbTransaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbAccount dbAccount = (DbAccount) o;

        if (id != null ? !id.equals(dbAccount.id) : dbAccount.id != null) return false;
        if (fullName != null ? !fullName.equals(dbAccount.fullName) : dbAccount.fullName != null) return false;
        if (registrationDate != null ? !registrationDate.equals(dbAccount.registrationDate) : dbAccount.registrationDate != null)
            return false;
        return transactions != null ? transactions.equals(dbAccount.transactions) : dbAccount.transactions == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (registrationDate != null ? registrationDate.hashCode() : 0);
        result = 31 * result + (transactions != null ? transactions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DbAccount{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", registrationDate=" + registrationDate +
                ", dbTransactions=" + transactions +
                '}';
    }

}