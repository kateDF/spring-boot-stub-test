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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
    @ValueSource(longs = {10000, 10001})
    public void testSuccessFindAccountById(Long id) {
        ResponseEntity<ApiAccount> response = restTemplate.getForEntity("http://localhost:" + port + "/accounts" +
                "/" + id, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify account id").isEqualTo(id);
    }

    @Test
    public void testSuccessCreateAccount() {
        ApiAccount originalApiAccount = new ApiAccount(10005L, "Li Tang", null, null, Arrays.asList(
                new ApiTransaction("Paycheck", 900),
                new ApiTransaction("Food", 102.16)
        ));
        ApiAccount expectedApiAccount = new ApiAccount(10005L, "Li Tang", LocalDate.now(),
                new ApiBalance(1002.16, 1002.16 * EUR_RATE), Arrays.asList(
                new ApiTransaction("Paycheck", 900),
                new ApiTransaction("Food", 102.16)
        ));
        ResponseEntity<ApiAccount> response = restTemplate.postForEntity("http://localhost:" + port + "/accounts",
                originalApiAccount, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody()).as("Verify account has been created").isEqualToComparingFieldByField(expectedApiAccount);
    }

    @Test
    public void testSuccessAddTransaction() {
        long idAccount = 10002L;
        ApiAccount startAccount = getRequestApiAccountById(idAccount);
        ApiTransaction apiTransaction = new ApiTransaction("Paycheck", 1000);
        double expectedUsdBalance = startAccount.getBalance().getUsdBalance() + 1000;

        ResponseEntity<ApiBalance> response = restTemplate.postForEntity("http://localhost:" + port + "/accounts" +
                "/" + idAccount + "/transactions", apiTransaction, ApiBalance.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getUsdBalance()).as("Verify usd balance after transaction adding").isEqualTo(expectedUsdBalance);
        assertThat(response.getBody().getEuroBalance()).as("Verify euro balance after transaction adding").isEqualTo(expectedUsdBalance * EUR_RATE);
    }

    @Test
    public void testSuccessUpdateAccount() {
        ApiAccount account = new ApiAccount(10001L, "Marry R", LocalDate.now().minusDays(4),
                new ApiBalance(240.2, 240.2 * EUR_RATE), Arrays.asList(
                new ApiTransaction("Other", 100),
                new ApiTransaction("Check", 140.2)));
        RequestEntity<ApiAccount> requestEntity = RequestEntity
                .put(URI.create("http://localhost:" + port + "/accounts"))
                .body(account);
        ResponseEntity<ApiAccount> response = restTemplate.exchange(requestEntity, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify id").isEqualTo(account.getId());
        assertThat(response.getBody().getFullName()).as("Verify full name").isEqualTo(account.getFullName());
        assertThat(response.getBody().getBalance()).as("Verify balance").isEqualTo(account.getBalance());
        assertThat(response.getBody().getTransactions()).as("Verify transactions").isEqualTo(account.getTransactions());
    }

    @Test
    public void successDeleteAccount() {
        long idAccount = 10001L;
        RequestEntity<ApiAccount> requestEntity = new RequestEntity<>(HttpMethod.DELETE, URI.create("http://localhost" +
                ":" + port + "/accounts" + "/" + idAccount));
        ResponseEntity<ApiAccount> response = restTemplate.exchange(requestEntity, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify account has been deleted").isEqualTo(idAccount);

    }

    private ApiAccount getRequestApiAccountById(Long id) {
        return restTemplate.getForObject("http://localhost:" + port + "/accounts" + "/" + id, ApiAccount.class);
    }

    private String getBodyForCurrencyStub() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(new File(EXCHANGE_RATE_RESPONSE_TEMPLATE)).toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not prepare stub", e);
        }
    }

}
