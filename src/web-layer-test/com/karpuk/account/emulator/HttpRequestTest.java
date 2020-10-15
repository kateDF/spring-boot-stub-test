package com.karpuk.account.emulator;

import com.karpuk.account.emulator.api.model.ApiAccount;
import com.karpuk.account.emulator.api.model.ApiBalance;
import com.karpuk.account.emulator.api.model.ApiTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {

    private static double EURO_EXCHANGE_RATE = 0.85;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetAccounts() {
        ResponseEntity<List<ApiAccount>> response = restTemplate.exchange("http://localhost:" + port + "/accounts", HttpMethod.GET,
                null, new ParameterizedTypeReference<List<ApiAccount>>() {
                });
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody()).as("Verify accounts list not empty by default").isNotNull();
    }

    @ParameterizedTest(name = "{index} => id={0}")
    @ValueSource(longs = {10000, 10001})
    public void testSuccessFindAccountById(Long id) {
        ResponseEntity<ApiAccount> response = restTemplate.getForEntity("http://localhost:" + port + "/accounts" + "/" + id, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody().getId()).as("Verify account id").isEqualTo(id);
    }

    @Test
    public void testSuccessCreateAccount() {
        ApiAccount originalApiAccount = new ApiAccount(10005L, "Li Tang", null, null, Arrays.asList(
                new ApiTransaction("Paycheck", 900),
                new ApiTransaction("Food", 102.16)
        ));
        ApiAccount expectedApiAccount = new ApiAccount(10005L, "Li Tang", LocalDate.now(), new ApiBalance(1002.16, 1002.16 * EURO_EXCHANGE_RATE), Arrays.asList(
                new ApiTransaction("Paycheck", 900),
                new ApiTransaction("Food", 102.16)
        ));
        ResponseEntity<ApiAccount> response = restTemplate.postForEntity("http://localhost:" + port + "/accounts", originalApiAccount, ApiAccount.class);
        assertThat(response.getStatusCodeValue()).as("Verify status code").isEqualTo(200);
        assertThat(response.getBody()).as("Verify that exact account has been created").isEqualToComparingFieldByField(expectedApiAccount);
    }

}
