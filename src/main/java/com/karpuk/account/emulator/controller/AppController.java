package com.karpuk.account.emulator.controller;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.db.model.DbAccount;
import com.karpuk.account.emulator.db.model.DbTransaction;
import com.karpuk.account.emulator.db.repository.DbAccountMongoRepository;
import com.karpuk.account.emulator.upstream.currency.client.CurrencyExchangeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@EnableAutoConfiguration
public class AppController {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private CurrencyExchangeClient currencyExchangeClient;

    @Autowired
    private DbAccountMongoRepository dbAccountMongoRepository;

    @GetMapping("/accounts/{id}")
    public ApiAccount getAccountById(@PathVariable("id") Long id) {
        Optional<DbAccount> dbAccount = dbAccountMongoRepository.findById(id);
        return accountMapper.mapToApiAccount(dbAccount.get(), currencyExchangeClient.getUsdEuroRate());
    }

    @GetMapping("/accounts")
    public List<ApiAccount> getAccounts() {
        List<ApiAccount> apiAccounts = new ArrayList<>();
        for (DbAccount dbAccount : dbAccountMongoRepository.findAll()) {
            apiAccounts.add(accountMapper.mapToApiAccount(dbAccount, currencyExchangeClient.getUsdEuroRate()));
        }
        return apiAccounts;
    }

    @PostMapping("/accounts")
    private ApiAccount createAccount(@RequestBody ApiAccount apiAccount) {
        DbAccount dbAccount = accountMapper.mapToDbAccount(apiAccount);
        DbAccount createdAccount = dbAccountMongoRepository.save(dbAccount);
        return accountMapper.mapToApiAccount(createdAccount, currencyExchangeClient.getUsdEuroRate());
    }

    @PostMapping("/accounts/{id}/transactions")
    private ApiBalance addTransaction(@RequestBody ApiTransaction apiTransaction, @PathVariable("id") Long id) {
        Optional<DbAccount> dbAccount = dbAccountMongoRepository.findById(id);
        DbTransaction dbTransaction = accountMapper.mapToDbTransaction(apiTransaction);
        dbAccount.get().addTransaction(dbTransaction);
        DbAccount updatedAccount = dbAccountMongoRepository.save(dbAccount.get());
        ApiAccount apiAccount = accountMapper.mapToApiAccount(updatedAccount, currencyExchangeClient.getUsdEuroRate());
        return apiAccount.getBalance();
    }

    @PutMapping("/accounts")
    private ApiAccount updateAccount(@RequestBody ApiAccount apiAccount) {
        DbAccount dbAccount = accountMapper.mapToDbAccount(apiAccount);
        DbAccount updatedAccount = dbAccountMongoRepository.save(dbAccount);
        return accountMapper.mapToApiAccount(updatedAccount, currencyExchangeClient.getUsdEuroRate());
    }

    @DeleteMapping("/accounts/{id}")
    private void deleteAccount(@PathVariable("id") Long id) {
        Optional<DbAccount> dbAccount = dbAccountMongoRepository.findById(id);
        dbAccountMongoRepository.delete(dbAccount.get());
    }

}
