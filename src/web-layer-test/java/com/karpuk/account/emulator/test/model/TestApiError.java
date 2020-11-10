package com.karpuk.account.emulator.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestApiError {

    private Timestamp timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

}
