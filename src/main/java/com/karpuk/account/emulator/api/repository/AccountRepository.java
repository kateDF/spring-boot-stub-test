package com.karpuk.account.emulator.api.repository;

import com.karpuk.account.emulator.api.model.Account;
import com.karpuk.account.emulator.api.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AccountRepository {

    private Map<Long, Account> accounts;

    public AccountRepository() {
        accounts = new HashMap<>();
        accounts.put(10000L, new Account(10000L, "John Summer", Arrays.asList(
                new Transaction("Grocery", 110.88),
                new Transaction("Other", 242.46)
        )));
        accounts.put(10001L, new Account(10001L, "Mary Nun", Arrays.asList(
                new Transaction("Travel", 1222.80)
        )));
        accounts.put(10002L, new Account(10002L, "Nick Woods", Arrays.asList(
                new Transaction("Grocery", 88.12),
                new Transaction("Travel", -586.1)
        )));
    }

    public Map<Long, Account> getAccounts() {
        return accounts;
    }

    public Account findAccountById(Long id) {
        if (accounts.containsKey(id)) {
            return accounts.get(id);
        } else {
            throw new IllegalArgumentException("No account found with " + id + " id");
        }
    }

    public Account addAccount(Account account) {
        if (!accounts.containsKey(account.getId())) {
            accounts.put(account.getId(), account);
            return account;
        } else {
            throw new IllegalArgumentException("User with " + account.getId() + " id has already exists.");
        }
    }

}
