package com.karpuk.account.emulator.db.model;

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
public class DbAccount {

    @Id
    private String id;
    private String fullName;
    private LocalDate registrationDate = LocalDate.now();
    private List<DbTransaction> transactions;

    public void addTransaction(DbTransaction newTransaction) {
        transactions.add(newTransaction);
    }

}
