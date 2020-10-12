package com.karpuk.account.emulator.api.controller;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.db.model.DbAccount;
import com.karpuk.account.emulator.db.repository.DbAccountRepository;
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

    @GetMapping("/accounts/{id}")
    public ApiAccount getAccountById(@PathVariable("id") Long id) {
        DbAccount dbAccount = accountRepository.findAccountById(id);
        return AccountMapper.mapToApiAccount(dbAccount);
    }

    @GetMapping("/accounts")
    public List<ApiAccount> getAccountsRepository() {
        List<ApiAccount> apiAccounts = new ArrayList<>();
        for (DbAccount dbAccount : accountRepository.getAccounts().values()) {
            apiAccounts.add(AccountMapper.mapToApiAccount(dbAccount));
        }
        return apiAccounts;
    }

    @PostMapping("/accounts")
    private ApiAccount createAccount(@RequestBody DbAccount dbAccount){
        return AccountMapper.mapToApiAccount(accountRepository.addAccount(dbAccount));
    }

}
