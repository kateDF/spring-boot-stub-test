package com.karpuk.account.emulator.api.controller;

import com.karpuk.account.emulator.api.model.Account;
import com.karpuk.account.emulator.api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class AppController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/accounts/{id}")
    @ResponseBody
    public Account getAccountById(@PathVariable("id") Long id) {
        return accountRepository.findAccountById(id);
    }

}
