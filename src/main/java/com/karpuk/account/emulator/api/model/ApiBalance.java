package com.karpuk.account.emulator.api.model;

public class ApiBalance {

    private double usdBalance;
    private double euroBalance;

    public ApiBalance(double usdBalance, double euroBalance) {
        this.usdBalance = usdBalance;
        this.euroBalance = euroBalance;
    }

    public double getUsdBalance() {
        return usdBalance;
    }

    public void setUsdBalance(double usdBalance) {
        this.usdBalance = usdBalance;
    }

    public double getEuroBalance() {
        return euroBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApiBalance apiBalance = (ApiBalance) o;

        if (Double.compare(apiBalance.usdBalance, usdBalance) != 0) return false;
        return Double.compare(apiBalance.euroBalance, euroBalance) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(usdBalance);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(euroBalance);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String
    toString() {
        return "Balance{" +
                "usdBalance=" + usdBalance +
                ", euroBalance=" + euroBalance +
                '}';
    }

}
