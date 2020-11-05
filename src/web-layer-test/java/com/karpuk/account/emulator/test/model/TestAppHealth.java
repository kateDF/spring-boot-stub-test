package com.karpuk.account.emulator.test.model;

import org.springframework.boot.actuate.health.Status;

public class TestAppHealth {

    private Status status;

    public TestAppHealth() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
