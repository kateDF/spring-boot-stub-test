package com.karpuk.account.emulator.db.repository;

import com.karpuk.account.emulator.db.model.DbAccount;
import com.karpuk.account.emulator.db.model.DbTransaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DbAccountRepository {

    private Map<Long, DbAccount> accounts;

    public DbAccountRepository() {
        accounts = new HashMap<>();
        accounts.put(10000L, new DbAccount(10000L, "John Summer", LocalDate.now().minusDays(10), getListTransactions(
                new DbTransaction("Grocery", 110.88),
                new DbTransaction("Other", 242.46)
        )));
        accounts.put(10001L, new DbAccount(10001L, "Mary Nun", LocalDate.now().minusDays(5), getListTransactions(
                new DbTransaction("Travel", 1222.80)
        )));
        accounts.put(10002L, new DbAccount(10002L, "Nick Woods", LocalDate.now().minusDays(2), getListTransactions(
                new DbTransaction("Grocery", 88.12),
                new DbTransaction("Travel", -586.1)
        )));
    }

    public Map<Long, DbAccount> getAccounts() {
        return accounts;
    }

    public DbAccount findAccountById(Long id) {
        if (accounts.containsKey(id)) {
            return accounts.get(id);
        } else {
            throw new IllegalArgumentException("No account found with " + id + " id");
        }
    }

    public DbAccount addAccount(DbAccount account) {
        if (!accounts.containsKey(account.getId())) {
            accounts.put(account.getId(), account);
            return account;
        } else {
            throw new IllegalArgumentException("User with " + account.getId() + " id has already exists.");
        }
    }

    private List<DbTransaction> getListTransactions(DbTransaction... transactions) {
        if (transactions == null) {
            return null;
        }
        List<DbTransaction> transactionList = new ArrayList<>();
        for (DbTransaction tr : transactions) {
            transactionList.add(tr);
        }
        return transactionList;
    }

}
