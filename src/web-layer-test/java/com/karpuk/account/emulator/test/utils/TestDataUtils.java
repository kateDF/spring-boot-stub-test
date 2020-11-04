package com.karpuk.account.emulator.test.utils;

import wiremock.org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public class TestDataUtils {

    public static String getRandomName(int length) {
        return RandomStringUtils.random(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    public static Double getRandomTransactionValue() {
        Random r = new Random();
        return -1000 + (1000 - -1000) * r.nextDouble();
    }

}
