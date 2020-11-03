package com.karpuk.account.emulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.test.client.TestClient;
import com.karpuk.account.emulator.test.model.TestDbAccount;
import com.karpuk.account.emulator.test.model.TestDbTransaction;
import com.karpuk.account.emulator.test.utils.TestAccountMapper;
import com.karpuk.account.emulator.upstream.currency.client.CurrencyExchangeClient;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import wiremock.org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBootAppLauncher.class)
@AutoConfigureWireMock(port = 8090)
@ActiveProfiles("test-stub")
public class HttpRequestTest {

    private static final double EUR_RATE = 0.8467400508;
    private static final String EXCHANGE_RATE_RESPONSE_TEMPLATE = "src/web-layer-test/resources/rate-response.json";

    @LocalServerPort
    private int port;

    @Autowired
    private TestClient testClient;
    @Autowired
    private CurrencyExchangeClient currencyExchangeClient;
    @Autowired
    private TestAccountMapper testAccountMapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void setUp() {
        stubFor(get(urlEqualTo("/latest?base=USD")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(getBodyForCurrencyStub())));
    }

    @Test
    public void testGetAccounts() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(createRandomAccountInDb(), EUR_RATE);
        long expectedDbSize = getDbSize();
        ResponseEntity<List<ApiAccount>> response = testClient.getAllDbAccounts(port);
        long actualSize = response.getBody().size();

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(actualSize).as("Verify db size").isEqualTo(expectedDbSize);
        assertThat(response.getBody()).as("Verify original account").contains(originalApiAccount);
    }

    @Test
    public void testSuccessFindAccountById() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(createRandomAccountInDb(), EUR_RATE);
        ResponseEntity<ApiAccount> response = testClient.getAccountById(port, originalApiAccount.getId());

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody()).as("Verify account").isEqualToComparingFieldByField(originalApiAccount);
    }

    @Test
    public void testSuccessCreateAccount() {
        TestDbAccount testDbAccount = new TestDbAccount(null, getRandomName(10), null, Arrays.asList(
                new TestDbTransaction("Paycheck", getRandomTransactionValue()),
                new TestDbTransaction("Food", getRandomTransactionValue())
        ));
        ApiAccount expectedApiAccount = testAccountMapper.mapToApiAccount(testDbAccount, EUR_RATE);
        ResponseEntity<ApiAccount> response = testClient.postAccount(port, expectedApiAccount);

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getFullName()).as("Verify full name").isEqualTo(expectedApiAccount.getFullName());
        assertThat(response.getBody().getBalance()).as("Verify balance").isEqualTo(expectedApiAccount.getBalance());
        assertThat(response.getBody().getTransactions()).as("Verify transactions").isEqualTo(expectedApiAccount.getTransactions());
    }

    @Test
    public void testSuccessAddTransaction() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(createRandomAccountInDb(), EUR_RATE);
        ApiTransaction apiTransaction = new ApiTransaction("Paycheck", getRandomTransactionValue());
        double expectedUsdBalance = originalApiAccount.getBalance().getUsdBalance() + apiTransaction.getAmount();
        ResponseEntity<ApiBalance> response = testClient.addTransaction(port, originalApiAccount.getId(),
                apiTransaction);

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getUsdBalance()).as("Verify usd balance").isEqualTo(expectedUsdBalance, Offset.offset(0.01));
        assertThat(response.getBody().getEuroBalance()).as("Verify euro balance").isEqualTo(expectedUsdBalance * EUR_RATE, Offset.offset(0.01));
    }

    @Test
    public void testSuccessUpdateAccount() {
        ApiAccount apiAccount = testAccountMapper.mapToApiAccount(createRandomAccountInDb(), EUR_RATE);
        String newFullName = getRandomName(10);
        apiAccount.setFullName(newFullName);
        ResponseEntity<ApiAccount> response = testClient.updateAccount(port, apiAccount);

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify id").isEqualTo(apiAccount.getId());
        assertThat(response.getBody().getFullName()).as("Verify full name").isEqualTo(apiAccount.getFullName());
        assertThat(response.getBody().getBalance()).as("Verify balance").isEqualTo(apiAccount.getBalance());
        assertThat(response.getBody().getTransactions()).as("Verify transactions").isEqualTo(apiAccount.getTransactions());
    }

    @Test
    public void successDeleteAccount() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(createRandomAccountInDb(), EUR_RATE);
        ResponseEntity<ApiAccount> response = testClient.deleteAccount(port, originalApiAccount.getId());

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

    private TestDbAccount createRandomAccountInDb() {
        TestDbAccount dbObject = mongoTemplate.insert(new TestDbAccount(null, getRandomName(10), LocalDate.now(),
                Arrays.asList(
                        new TestDbTransaction("Other", getRandomTransactionValue()),
                        new TestDbTransaction("Paycheck", getRandomTransactionValue()))));
        return dbObject;
    }

    private long getDbSize() {
        long documentsCount = mongoTemplate.getCollection("accounts").countDocuments();
        return documentsCount;
    }

    private String getRandomName(int length) {
        return RandomStringUtils.random(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
    }

    private Double getRandomTransactionValue() {
        Random r = new Random();
        return -1000 + (1000 - -1000) * r.nextDouble();
    }

}
