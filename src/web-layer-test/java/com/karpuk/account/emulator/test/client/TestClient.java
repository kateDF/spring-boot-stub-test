package com.karpuk.account.emulator.test.client;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class TestClient {

    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<List<ApiAccount>> getAllDbAccounts(int port) {
        return restTemplate.exchange("http://localhost:" + port + "/accounts",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ApiAccount>>() {
                });
    }

    public ResponseEntity<ApiAccount> getAccountById(int port, String id) {
        return restTemplate.getForEntity("http://localhost:" + port + "/accounts" +
                "/" + id, ApiAccount.class);
    }

    public ResponseEntity<ApiAccount> postAccount(int port, ApiAccount account) {
        return restTemplate.postForEntity("http://localhost:" + port + "/accounts",
                account, ApiAccount.class);
    }

    public ResponseEntity<ApiBalance> addTransaction(int port, String accountId,
            ApiTransaction transaction) {
        return restTemplate.postForEntity("http://localhost:" + port + "/accounts" +
                "/" + accountId + "/transactions", transaction, ApiBalance.class);
    }

    public ResponseEntity<ApiAccount> updateAccount(int port, ApiAccount account) {
        RequestEntity<ApiAccount> requestEntity = RequestEntity
                .put(URI.create("http://localhost:" + port + "/accounts"))
                .body(account);
        return restTemplate.exchange(requestEntity, ApiAccount.class);
    }

    public ResponseEntity<ApiAccount> deleteAccount(int port, String id) {
        RequestEntity<ApiAccount> requestEntity = new RequestEntity<>(HttpMethod.DELETE, URI.create("http://localhost" +
                ":" + port + "/accounts" + "/" + id));
        return restTemplate.exchange(requestEntity, ApiAccount.class);
    }

}
