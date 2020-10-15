package com.karpuk.account.emulator.api.controller;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.db.model.DbAccount;
import com.karpuk.account.emulator.db.model.DbTransaction;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AccountMapper {

    public AccountMapper() {
    }

    public ApiAccount mapToApiAccount(DbAccount dbAccount, double euroExchangeRate) {
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setId(dbAccount.getId());
        apiAccount.setFullName(dbAccount.getFullName());
        apiAccount.setRegistrationDate(dbAccount.getRegistrationDate());
        double usdAmount = calculateBalance(dbAccount.getTransactions());
        apiAccount.setBalance(new ApiBalance(usdAmount, usdAmount * euroExchangeRate));
        apiAccount.setTransactions(mapToApiTransactions(dbAccount.getTransactions()));
        return apiAccount;
    }

    public DbAccount mapToDbAccount(ApiAccount apiAccount) {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setId(apiAccount.getId());
        dbAccount.setFullName(apiAccount.getFullName());
        if (apiAccount.getRegistrationDate() != null) {
            dbAccount.setRegistrationDate(apiAccount.getRegistrationDate());
        } else {
            dbAccount.setRegistrationDate(LocalDate.now());
        }
        dbAccount.setTransactions(mapToDbTransaction(apiAccount.getTransactions()));
        return dbAccount;
    }

    public DbTransaction mapToDbTransaction(ApiTransaction apiTransaction) {
        return new DbTransaction(apiTransaction.getType(), apiTransaction.getAmount());
    }

    private List<ApiTransaction> mapToApiTransactions(List<DbTransaction> dbTransactions) {
        if (dbTransactions == null) {
            return null;
        }
        List<ApiTransaction> apiTransactions = new ArrayList<>();
        for (DbTransaction dbTransaction : dbTransactions) {
            apiTransactions.add(new ApiTransaction(dbTransaction.getType(), dbTransaction.getAmount()));
        }
        return apiTransactions;
    }

    private List<DbTransaction> mapToDbTransaction(List<ApiTransaction> apiTransactions) {
        List<DbTransaction> dbTransactions = new ArrayList<>();
        for (ApiTransaction apiTransaction : apiTransactions) {
            dbTransactions.add(mapToDbTransaction(apiTransaction));
        }
        return dbTransactions;
    }

    private double calculateBalance(List<DbTransaction> dbTransactions) {
        if (dbTransactions == null || dbTransactions.isEmpty()) {
            return 0;
        }
        return dbTransactions.stream()
                .filter(Objects::nonNull)
                .map(DbTransaction::getAmount)
                .collect(Collectors.summingDouble(Double::doubleValue));
    }

}
