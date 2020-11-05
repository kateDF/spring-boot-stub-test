package com.karpuk.account.emulator.test.client;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.test.model.TestAppHealth;
import com.karpuk.account.emulator.test.model.TestAppInfo;
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

    @Value("${test.endpoints.actuator.health}")
    private String appHealthEndpoint;
    @Value("${test.endpoints.actuator.info}")
    private String appInfoEndpoint;

    @Value("${test.endpoints.accounts}")
    private String accountsEndpoint;
    @Value("${test.endpoints.account}")
    private String exactAccountEndpoint;
    @Value("${test.endpoints.transactions}")
    private String transactionsEndpoint;

    @Autowired
    private TestRestTemplate restTemplate;

    public ResponseEntity<TestAppHealth> getAppHealth() {
        return restTemplate.getForEntity(appHealthEndpoint, TestAppHealth.class);
    }

    public ResponseEntity<TestAppInfo> getAppInfo() {
        return restTemplate.getForEntity(appInfoEndpoint, TestAppInfo.class);
    }

    public ResponseEntity<List<ApiAccount>> getAllDbAccounts() {
        return restTemplate.exchange(accountsEndpoint, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ApiAccount>>() {
                });
    }

    public ResponseEntity<ApiAccount> getAccountById(String id) {
        return restTemplate.getForEntity(exactAccountEndpoint.replace("{accountId}", id), ApiAccount.class);
    }

    public ResponseEntity<ApiAccount> postAccount(ApiAccount account) {
        return restTemplate.postForEntity(accountsEndpoint, account, ApiAccount.class);
    }

    public ResponseEntity<ApiBalance> addTransaction(String accountId,
            ApiTransaction transaction) {
        return restTemplate.postForEntity(transactionsEndpoint.replace("{accountId}", accountId),
                transaction, ApiBalance.class);
    }

    public ResponseEntity<ApiAccount> updateAccount(ApiAccount account) {
        RequestEntity<ApiAccount> requestEntity = RequestEntity
                .put(URI.create(accountsEndpoint))
                .body(account);
        return restTemplate.exchange(requestEntity, ApiAccount.class);
    }

    public ResponseEntity<ApiAccount> deleteAccount(String id) {
        RequestEntity<ApiAccount> requestEntity = new RequestEntity<>(HttpMethod.DELETE,
                URI.create(exactAccountEndpoint.replace("{accountId}", id)));
        return restTemplate.exchange(requestEntity, ApiAccount.class);
    }

}
