package com.karpuk.account.emulator.db.model;

public class DbTransaction {

    private String type;
    private double amount;

    public DbTransaction() {
    }

    public DbTransaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbTransaction that = (DbTransaction) o;

        if (Double.compare(that.amount, amount) != 0) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = type != null ? type.hashCode() : 0;
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DbTransaction{" +
                "type='" + type + '\'' +
                ", amount=" + amount +
                '}';
    }

}
