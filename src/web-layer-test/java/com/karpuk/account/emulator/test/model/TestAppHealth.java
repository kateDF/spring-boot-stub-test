package com.karpuk.account.emulator.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.actuate.health.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestAppHealth {

    private Status status;

}
