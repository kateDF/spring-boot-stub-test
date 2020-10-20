package com.karpuk.account.emulator.controller;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.db.model.DbAccount;
import com.karpuk.account.emulator.db.model.DbTransaction;
import com.karpuk.account.emulator.db.repository.DbAccountRepository;
import com.karpuk.account.emulator.upstream.currency.client.CurrencyExchangeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@EnableAutoConfiguration
public class AppController {

    @Autowired
    private DbAccountRepository accountRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CurrencyExchangeClient currencyExchangeClient;


    @GetMapping("/accounts/{id}")
    public ApiAccount getAccountById(@PathVariable("id") Long id) {
        DbAccount dbAccount = accountRepository.findAccountById(id);
        return accountMapper.mapToApiAccount(dbAccount, currencyExchangeClient.getUsdEuroRate());
    }

    @GetMapping("/accounts")
    public List<ApiAccount> getAccounts() {
        List<ApiAccount> apiAccounts = new ArrayList<>();
        for (DbAccount dbAccount : accountRepository.getAccounts().values()) {
            apiAccounts.add(accountMapper.mapToApiAccount(dbAccount, currencyExchangeClient.getUsdEuroRate()));
        }
        return apiAccounts;
    }

    @PostMapping("/accounts")
    private ApiAccount createAccount(@RequestBody ApiAccount apiAccount) {
        DbAccount dbAccount = accountMapper.mapToDbAccount(apiAccount);
        DbAccount createdAccount = accountRepository.addAccount(dbAccount);
        return accountMapper.mapToApiAccount(createdAccount, currencyExchangeClient.getUsdEuroRate());
    }

    @PostMapping("/accounts/{id}/transactions")
    private ApiBalance addTransaction(@RequestBody ApiTransaction apiTransaction, @PathVariable("id") Long id) {
        DbAccount account = accountRepository.findAccountById(id);
        DbTransaction dbTransaction = accountMapper.mapToDbTransaction(apiTransaction);
        account.addTransaction(dbTransaction);
        //TODO execute update db record
        ApiAccount apiAccount = accountMapper.mapToApiAccount(account, currencyExchangeClient.getUsdEuroRate());
        return apiAccount.getBalance();
    }

    @PutMapping("/accounts")
    private ApiAccount updateAccount(@RequestBody ApiAccount apiAccount) {
        DbAccount dbAccount = accountMapper.mapToDbAccount(apiAccount);
        DbAccount updatedAccount = accountRepository.updateAccount(dbAccount);
        return accountMapper.mapToApiAccount(updatedAccount, currencyExchangeClient.getUsdEuroRate());
    }

    @DeleteMapping("/accounts/{id}")
    private ApiAccount deleteAccount(@PathVariable("id") Long id) {
        DbAccount deletedAccount = accountRepository.deleteAccountById(id);
        return accountMapper.mapToApiAccount(deletedAccount, currencyExchangeClient.getUsdEuroRate());
    }

}
