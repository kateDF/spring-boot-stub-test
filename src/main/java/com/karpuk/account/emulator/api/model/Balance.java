package com.karpuk.account.emulator.api.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Balance {

    private static double EURO_EXCHANGE_RATE = 0.85;

    private double usdBalance;
    private double euroBalance;

    public Balance(List<Transaction> transactions) {
        this.usdBalance = transactions.stream()
                .filter(Objects::nonNull)
                .map(Transaction::getAmount)
                .collect(Collectors.summingDouble(Double::doubleValue));
        this.euroBalance = usdBalance * EURO_EXCHANGE_RATE;
    }


    public Balance(double usdBalance) {
        this.usdBalance = usdBalance;
        this.euroBalance = usdBalance * EURO_EXCHANGE_RATE;
    }

    public double getUsdBalance() {
        return usdBalance;
    }

    public double getEuroBalance() {
        return euroBalance;
    }

    public static double getEuroExchangeRate() {
        return EURO_EXCHANGE_RATE;
    }

    public void setUsdBalance(double usdBalance) {
        this.usdBalance = usdBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Balance balance = (Balance) o;

        if (Double.compare(balance.usdBalance, usdBalance) != 0) return false;
        return Double.compare(balance.euroBalance, euroBalance) == 0;
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
