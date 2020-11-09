package com.karpuk.account.emulator.test.utils;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.test.model.TestDbAccount;
import com.karpuk.account.emulator.test.model.TestDbTransaction;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TestAccountMapper {

    public TestAccountMapper() {
    }

    public ApiAccount mapToApiAccount(TestDbAccount testDbAccount, double euroExchangeRate) {
        ApiAccount apiAccount = new ApiAccount();
        apiAccount.setId(testDbAccount.getId());
        apiAccount.setFullName(testDbAccount.getFullName());
        apiAccount.setRegistrationDate(testDbAccount.getRegistrationDate());
        double usdAmount = calculateBalance(testDbAccount.getTransactions());
        apiAccount.setBalance(new ApiBalance(usdAmount, getFormattedEuroBalance(usdAmount * euroExchangeRate)));
        apiAccount.setTransactions(mapToApiTransactions(testDbAccount.getTransactions()));
        return apiAccount;
    }

    public TestDbAccount mapToTestDbAccount(ApiAccount apiAccount) {
        TestDbAccount testDbAccount = new TestDbAccount();
        testDbAccount.setId(apiAccount.getId());
        testDbAccount.setFullName(apiAccount.getFullName());
        if (apiAccount.getRegistrationDate() != null) {
            testDbAccount.setRegistrationDate(apiAccount.getRegistrationDate());
        } else {
            testDbAccount.setRegistrationDate(LocalDate.now());
        }
        testDbAccount.setTransactions(mapToTestDbTransaction(apiAccount.getTransactions()));
        return testDbAccount;
    }

    public TestDbTransaction mapToTestDbTransaction(ApiTransaction apiTransaction) {
        return new TestDbTransaction(apiTransaction.getType(), apiTransaction.getAmount());
    }

    private List<ApiTransaction> mapToApiTransactions(List<TestDbTransaction> testDbTransactions) {
        if (testDbTransactions == null) {
            return null;
        }
        List<ApiTransaction> apiTransactions = new ArrayList<>();
        for (TestDbTransaction testDbTransaction : testDbTransactions) {
            apiTransactions.add(new ApiTransaction(testDbTransaction.getType(), testDbTransaction.getAmount()));
        }
        return apiTransactions;
    }

    private List<TestDbTransaction> mapToTestDbTransaction(List<ApiTransaction> apiTransactions) {
        List<TestDbTransaction> testDbTransactions = new ArrayList<>();
        for (ApiTransaction apiTransaction : apiTransactions) {
            testDbTransactions.add(mapToTestDbTransaction(apiTransaction));
        }
        return testDbTransactions;
    }

    private double calculateBalance(List<TestDbTransaction> testDbTransactions) {
        if (testDbTransactions == null || testDbTransactions.isEmpty()) {
            return 0;
        }
        return testDbTransactions.stream()
                .filter(Objects::nonNull)
                .map(TestDbTransaction::getAmount)
                .collect(Collectors.summingDouble(Double::doubleValue));
    }

    private double getFormattedEuroBalance(double eurBalance) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.valueOf(df.format(eurBalance));
    }

}
