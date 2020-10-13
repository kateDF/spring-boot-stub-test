package com.karpuk.account.emulator.api.controller;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.db.model.DbAccount;
import com.karpuk.account.emulator.db.model.DbTransaction;
import com.karpuk.account.emulator.db.repository.DbAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class AppController {

    private static double EURO_EXCHANGE_RATE = 0.85;

    @Autowired
    private DbAccountRepository accountRepository;
    @Autowired
    private AccountMapper accountMapper;

    @GetMapping("/accounts/{id}")
    public ApiAccount getAccountById(@PathVariable("id") Long id) {
        DbAccount dbAccount = accountRepository.findAccountById(id);
        return accountMapper.mapToApiAccount(dbAccount, EURO_EXCHANGE_RATE);
    }

    @GetMapping("/accounts")
    public List<ApiAccount> getAccountsRepository() {
        List<ApiAccount> apiAccounts = new ArrayList<>();
        for (DbAccount dbAccount : accountRepository.getAccounts().values()) {
            apiAccounts.add(accountMapper.mapToApiAccount(dbAccount, EURO_EXCHANGE_RATE));
        }
        return apiAccounts;
    }

    @PostMapping("/accounts")
    private ApiAccount createAccount(@RequestBody DbAccount dbAccount) {
        DbAccount createdAccount = accountRepository.addAccount(dbAccount);
        return accountMapper.mapToApiAccount(createdAccount, EURO_EXCHANGE_RATE);
    }

    @PostMapping("/accounts/{id}/transactions")
    private ApiBalance addTransaction(@RequestBody DbTransaction transaction, @PathVariable("id") Long id) {
        DbAccount account = accountRepository.findAccountById(id);
        account.addTransaction(transaction);
        //TODO execute update db record
        ApiAccount apiAccount = accountMapper.mapToApiAccount(account, EURO_EXCHANGE_RATE);
        return apiAccount.getBalance();
    }

}
