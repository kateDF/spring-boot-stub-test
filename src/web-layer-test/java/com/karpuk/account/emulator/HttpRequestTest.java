package com.karpuk.account.emulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.upstream.currency.client.CurrencyExchangeClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import wiremock.org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8090)
@ActiveProfiles("test-stub")
public class HttpRequestTest {

    private static final double EUR_RATE = 0.8467400508;
    private static final String EXCHANGE_RATE_RESPONSE_TEMPLATE = "src/web-layer-test/resources/rate-response.json";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private CurrencyExchangeClient currencyExchangeClient;

    @BeforeEach
    public void setUp() {
        stubFor(get(urlEqualTo("/latest?base=USD")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(getBodyForCurrencyStub())));
    }

    @Test
    public void testGetAccounts() {
        ResponseEntity<List<ApiAccount>> response = restTemplate.exchange("http://localhost:" + port + "/accounts",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<ApiAccount>>() {
                });
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody()).as("Verify accounts list not empty by default").isNotNull();
    }

    @ParameterizedTest(name = "{index} => id={0}")
    @ValueSource(strings = {"5f96e1c8f5a331684a5c4b49", "5f96e21ff5a331684a5c4b4a"})
    public void testSuccessFindAccountById(String id) {
        ResponseEntity<ApiAccount> response = restTemplate.getForEntity("http://localhost:" + port + "/accounts" +
                "/" + id, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify account id").isEqualTo(id);
    }

    @Test
    public void testSuccessCreateAccount() {
        String fullName = getRandomName(10);
        ApiAccount originalApiAccount = new ApiAccount(null, fullName, null, null, Arrays.asList(
                new ApiTransaction("Paycheck", 900),
                new ApiTransaction("Food", 102.16)
        ));
        ApiAccount expectedApiAccount = new ApiAccount(null, fullName, LocalDate.now(),
                new ApiBalance(1002.16, 1002.16 * EUR_RATE), Arrays.asList(
                new ApiTransaction("Paycheck", 900),
                new ApiTransaction("Food", 102.16)
        ));
        ResponseEntity<ApiAccount> response = restTemplate.postForEntity("http://localhost:" + port + "/accounts",
                originalApiAccount, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getFullName()).as("Verify full name").isEqualTo(expectedApiAccount.getFullName());
        assertThat(response.getBody().getBalance()).as("Verify balance").isEqualTo(expectedApiAccount.getBalance());
        assertThat(response.getBody().getTransactions()).as("Verify transactions").isEqualTo(expectedApiAccount.getTransactions());
    }

    @Test
    public void testSuccessAddTransaction() {
        ApiAccount originalApiAccount = createRandomAccountInDb();
        ApiTransaction apiTransaction = new ApiTransaction("Paycheck", getRandomTransactionValue());
        double expectedUsdBalance = originalApiAccount.getBalance().getUsdBalance() + apiTransaction.getAmount();

        ResponseEntity<ApiBalance> response = restTemplate.postForEntity("http://localhost:" + port + "/accounts" +
                "/" + originalApiAccount.getId() + "/transactions", apiTransaction, ApiBalance.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getUsdBalance()).as("Verify usd balance after transaction adding").isEqualTo(expectedUsdBalance);
        assertThat(response.getBody().getEuroBalance()).as("Verify euro balance after transaction adding").isEqualTo(expectedUsdBalance * EUR_RATE);
    }

    @Test
    public void testSuccessUpdateAccount() {
        ApiAccount originalApiAccount = createRandomAccountInDb();
        String newFullName = getRandomName(10);
        originalApiAccount.setFullName(newFullName);

        RequestEntity<ApiAccount> requestEntity = RequestEntity
                .put(URI.create("http://localhost:" + port + "/accounts"))
                .body(originalApiAccount);
        ResponseEntity<ApiAccount> response = restTemplate.exchange(requestEntity, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify id").isEqualTo(originalApiAccount.getId());
        assertThat(response.getBody().getFullName()).as("Verify full name").isEqualTo(originalApiAccount.getFullName());
        assertThat(response.getBody().getBalance()).as("Verify balance").isEqualTo(originalApiAccount.getBalance());
        assertThat(response.getBody().getTransactions()).as("Verify transactions").isEqualTo(originalApiAccount.getTransactions());
    }

    @Test
    public void successDeleteAccount() {
        ApiAccount originalApiAccount = createRandomAccountInDb();
        RequestEntity<ApiAccount> requestEntity = new RequestEntity<>(HttpMethod.DELETE, URI.create("http://localhost" +
                ":" + port + "/accounts" + "/" + originalApiAccount.getId()));
        ResponseEntity<ApiAccount> response = restTemplate.exchange(requestEntity, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
    }

    private String getBodyForCurrencyStub() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(new File(EXCHANGE_RATE_RESPONSE_TEMPLATE)).toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not prepare stub", e);
        }
    }

    private ApiAccount createRandomAccountInDb() {
        String fullName = getRandomName(10);
        ApiAccount account = new ApiAccount(null, fullName, null, null, Arrays.asList(
                new ApiTransaction("Other", getRandomTransactionValue()),
                new ApiTransaction("Paycheck", getRandomTransactionValue())));
        ResponseEntity<ApiAccount> response = restTemplate.postForEntity("http://localhost:" + port +
                "/accounts", account, ApiAccount.class);
        return response.getBody();
    }

    private String getRandomName(int length) {
        return RandomStringUtils.random(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    private Double getRandomTransactionValue() {
        Random r = new Random();
        return -1000 + (1000 - -1000) * r.nextDouble();
    }

}
