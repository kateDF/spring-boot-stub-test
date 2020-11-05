package com.karpuk.account.emulator.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
public class TestDbAccount {

    @Id
    private String id;
    private String fullName;
    private LocalDate registrationDate = LocalDate.now();
    private List<TestDbTransaction> transactions;

}
