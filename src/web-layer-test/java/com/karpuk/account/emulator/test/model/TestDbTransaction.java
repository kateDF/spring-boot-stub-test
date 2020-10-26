package com.karpuk.account.emulator.test.model;

public class TestDbTransaction {

    private String type;
    private double amount;

    public TestDbTransaction() {
    }

    public TestDbTransaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

}
