package com.karpuk.account.emulator.api.controller;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.db.repository.DbAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class AppController {

    @Autowired
    private DbAccountRepository accountRepository;

    @GetMapping("/accounts/{id}")
    public ApiAccount getAccountById(@PathVariable("id") Long id) {
        return null;
    }

}
