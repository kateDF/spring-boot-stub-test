package com.karpuk.account.emulator.api.controller;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.db.model.DbAccount;
import com.karpuk.account.emulator.db.model.DbTransaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AccountMapper {

    private static double EURO_EXCHANGE_RATE = 0.85;

    public static ApiAccount mapToApiAccount(DbAccount dbAccount) {
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setId(dbAccount.getId());
        apiAccount.setFullName(dbAccount.getFullName());
        apiAccount.setRegistrationDate(dbAccount.getRegistrationDate());
        double usdAmount = getTransactionsAmount(dbAccount.getDbTransactions());
        apiAccount.setApiBalance(new ApiBalance(usdAmount, usdAmount * EURO_EXCHANGE_RATE));
        apiAccount.setApiTransactions(mapToApiTransaction(dbAccount.getDbTransactions()));
        return apiAccount;
    }

    public static DbAccount mapToDbAccount(ApiAccount apiAccount) {
        DbAccount dbAccount = new DbAccount();
        dbAccount.setId(apiAccount.getId());
        dbAccount.setFullName(apiAccount.getFullName());
        if (apiAccount.getRegistrationDate() != null) {
            dbAccount.setRegistrationDate(apiAccount.getRegistrationDate());
        } else {
            dbAccount.setRegistrationDate(LocalDate.now());
        }
        dbAccount.setDbTransactions(mapToDbTransaction(apiAccount.getApiTransactions()));
        return dbAccount;
    }

    private static List<ApiTransaction> mapToApiTransaction(List<DbTransaction> dbTransactions) {
        List<ApiTransaction> apiTransactions = new ArrayList<>();
        for (DbTransaction dbTransaction : dbTransactions) {
            apiTransactions.add(new ApiTransaction(dbTransaction.getType(), dbTransaction.getAmount()));
        }
        return apiTransactions;
    }

    private static List<DbTransaction> mapToDbTransaction(List<ApiTransaction> apiTransactions) {
        List<DbTransaction> dbTransactions = new ArrayList<>();
        for (ApiTransaction apiTransaction : apiTransactions) {
            dbTransactions.add(new DbTransaction(apiTransaction.getType(), apiTransaction.getAmount()));
        }
        return dbTransactions;
    }

    private static double getTransactionsAmount(List<DbTransaction> dbTransactions) {
        return dbTransactions.stream()
                .filter(Objects::nonNull)
                .map(DbTransaction::getAmount)
                .collect(Collectors.summingDouble(Double::doubleValue));
    }

}
