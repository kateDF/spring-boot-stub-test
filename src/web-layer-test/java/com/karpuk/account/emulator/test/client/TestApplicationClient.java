package com.karpuk.account.emulator.test.client;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class TestApplicationClient {

    @Value("${test.base.url}")
    private String baseUrl;
    @Value(("${test.transactions.endpoint}"))
    private String transactionsEndpoint;

    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<List<ApiAccount>> getAllDbAccounts() {
        return restTemplate.exchange(baseUrl,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ApiAccount>>() {
                });
    }

    public ResponseEntity<ApiAccount> getAccountById(String id) {
        return restTemplate.getForEntity(baseUrl + "/" + id, ApiAccount.class);
    }

    public ResponseEntity<ApiAccount> postAccount(ApiAccount account) {
        return restTemplate.postForEntity(baseUrl, account, ApiAccount.class);
    }

    public ResponseEntity<ApiBalance> addTransaction(String accountId,
            ApiTransaction transaction) {
        return restTemplate.postForEntity(baseUrl + "/" + accountId + transactionsEndpoint, transaction, ApiBalance.class);
    }

    public ResponseEntity<ApiAccount> updateAccount(ApiAccount account) {
        RequestEntity<ApiAccount> requestEntity = RequestEntity
                .put(URI.create(baseUrl))
                .body(account);
        return restTemplate.exchange(requestEntity, ApiAccount.class);
    }

    public ResponseEntity<ApiAccount> deleteAccount(String id) {
        RequestEntity<ApiAccount> requestEntity = new RequestEntity<>(HttpMethod.DELETE,
                URI.create(baseUrl + "/" + id));
        return restTemplate.exchange(requestEntity, ApiAccount.class);
    }

}
