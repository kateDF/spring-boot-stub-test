package com.karpuk.account.emulator;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import com.karpuk.account.emulator.test.client.MongoDbClient;
import com.karpuk.account.emulator.test.client.TestApplicationClient;
import com.karpuk.account.emulator.test.model.*;
import com.karpuk.account.emulator.test.utils.TestAccountMapper;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.karpuk.account.emulator.test.utils.TestDataUtils.getRandomName;
import static com.karpuk.account.emulator.test.utils.TestDataUtils.getRandomTransactionValue;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringBootAppLauncher.class)
@AutoConfigureWireMock(port = 8090)
@ActiveProfiles("test-stub")
@TestPropertySource(locations = "/test-stub.properties")
@ComponentScan("com.karpuk.account.emulator.test")
public class HttpRequestTest {

    private static final double EUR_RATE = 0.8467400508;
    private static final String EXCHANGE_RATE_TEMPLATE = "src/web-layer-test/resources/json/rate-response.json";
    private static final String EXCHANGE_RATE_PARAMETERIZED_TEMPLATE = "src/web-layer-test/resources/json" +
            "/rate-response-parameterized.json";

    @Autowired
    private TestApplicationClient testClient;
    @Autowired
    private TestAccountMapper testAccountMapper;
    @Autowired
    private MongoDbClient mongoDbClient;

    @BeforeEach
    public void setUp() {
        stubFor(get(urlEqualTo("/latest?base=USD")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(getBodyForCurrencyStub(EXCHANGE_RATE_TEMPLATE))));
    }

    @Test
    public void testApplicationHealth() {
        ResponseEntity<TestAppHealth> response = testClient.getAppHealth();
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getStatus()).as("Verify application health status").isEqualTo(Status.UP);
    }

    @Test
    public void testApplicationVersion() {
        ResponseEntity<TestAppInfo> response = testClient.getAppInfo();
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getVersion()).as("Verify application version").isEqualTo("1.0-SNAPSHOT");
    }

    @Test
    public void testGetAccounts() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(mongoDbClient.createRandomAccountInDb(),
                EUR_RATE);
        long expectedDbSize = mongoDbClient.getDbSize();
        ResponseEntity<List<ApiAccount>> response = testClient.getAllDbAccounts();
        long actualSize = response.getBody().size();

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(actualSize).as("Verify db size").isEqualTo(expectedDbSize);
        assertThat(response.getBody()).as("Verify original account").contains(originalApiAccount);
    }

    @Test
    public void testSuccessFindAccountById() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(mongoDbClient.createRandomAccountInDb(),
                EUR_RATE);
        ResponseEntity<ApiAccount> response = testClient.getAccountById(originalApiAccount.getId());

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
        ResponseEntity<ApiAccount> response = testClient.postAccount(expectedApiAccount);

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getFullName()).as("Verify full name").isEqualTo(expectedApiAccount.getFullName());
        assertThat(response.getBody().getBalance()).as("Verify balance").isEqualTo(expectedApiAccount.getBalance());
        assertThat(response.getBody().getTransactions()).as("Verify transactions").isEqualTo(expectedApiAccount.getTransactions());
    }

    @Test
    public void testSuccessAddTransaction() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(mongoDbClient.createRandomAccountInDb(),
                EUR_RATE);
        ApiTransaction apiTransaction = new ApiTransaction("Paycheck", getRandomTransactionValue());
        double expectedUsdBalance = originalApiAccount.getBalance().getUsdBalance() + apiTransaction.getAmount();
        ResponseEntity<ApiBalance> response = testClient.addTransaction(originalApiAccount.getId(),
                apiTransaction);

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getUsdBalance()).as("Verify usd balance").isEqualTo(expectedUsdBalance,
                Offset.offset(0.01));
        assertThat(response.getBody().getEuroBalance()).as("Verify euro balance").isEqualTo(expectedUsdBalance * EUR_RATE, Offset.offset(0.01));
    }

    @Test
    public void testSuccessUpdateAccount() {
        ApiAccount apiAccount = testAccountMapper.mapToApiAccount(mongoDbClient.createRandomAccountInDb(), EUR_RATE);
        String newFullName = getRandomName(10);
        apiAccount.setFullName(newFullName);
        ResponseEntity<ApiAccount> response = testClient.updateAccount(apiAccount);

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify id").isEqualTo(apiAccount.getId());
        assertThat(response.getBody().getFullName()).as("Verify full name").isEqualTo(apiAccount.getFullName());
        assertThat(response.getBody().getBalance()).as("Verify balance").isEqualTo(apiAccount.getBalance());
        assertThat(response.getBody().getTransactions()).as("Verify transactions").isEqualTo(apiAccount.getTransactions());
    }

    @Test
    public void testSuccessDeleteAccount() {
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(mongoDbClient.createRandomAccountInDb(),
                EUR_RATE);
        ResponseEntity<ApiAccount> response = testClient.deleteAccount(originalApiAccount.getId());

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
    }

    @ParameterizedTest()
    @ValueSource(doubles = {0.0, 0.01, 0.99, 1, 999999.9})
    public void testGetBalanceWithDifferentRates(Double eurExchangeRate) {
        stubFor(get(urlEqualTo("/latest?base=USD")).willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(getBodyForCurrencyStub(EXCHANGE_RATE_PARAMETERIZED_TEMPLATE).replace("${eur_rate}",
                        eurExchangeRate.toString()))));
        ApiAccount originalApiAccount = testAccountMapper.mapToApiAccount(mongoDbClient.createRandomAccountInDb(),
                eurExchangeRate);
        ApiBalance expectedBalance = originalApiAccount.getBalance();

        ResponseEntity<ApiAccount> response = testClient.getAccountById(originalApiAccount.getId());
        ApiBalance actualBalance = response.getBody().getBalance();

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(actualBalance.getUsdBalance()).as("Verify usd balance").isEqualTo(expectedBalance.getUsdBalance());
        assertThat(actualBalance.getEuroBalance()).as("Verify euro balance").isEqualTo(expectedBalance.getEuroBalance());
    }

    @Test
    public void testExchangeRateServiceFailure() {
        stubFor(get(urlEqualTo("/latest?base=USD")).willReturn(aResponse()
                .withStatus(500)));
        ApiAccount apiAccount = testAccountMapper.mapToApiAccount(mongoDbClient.createRandomAccountInDb(), EUR_RATE);
        ResponseEntity<TestApiError> response = testClient.getAccountByIdError(apiAccount.getId());

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(500);
        assertThat(response.getBody().getError()).as("Verify error").isEqualTo("Internal Server Error");
    }

    @Test
    public void testGetInvalidPath() {
        ResponseEntity<TestApiError> response = testClient.getInvalidEndpoint();

        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(404);
        assertThat(response.getBody().getError()).as("Verify error").isEqualTo("Not Found");
    }

    private String getBodyForCurrencyStub(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Could not prepare stub", e);
        }
    }

}
