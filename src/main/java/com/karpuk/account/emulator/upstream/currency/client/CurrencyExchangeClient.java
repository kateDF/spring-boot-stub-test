package com.karpuk.account.emulator.upstream.currency.client;

import com.karpuk.account.emulator.upstream.currency.model.ResponseExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyExchangeClient {

    public CurrencyExchangeClient() {
    }

    @Autowired
    private RestTemplate restTemplate;

    @Value("${application.currency-exchange.url}")
    private String URL;
    private static final String BASE_VALUE = "USD";

    public double getUsdEuroRate() {
        ResponseEntity<ResponseExchangeRate> responseEntity = restTemplate.getForEntity(URL + "?base=" + BASE_VALUE,
                ResponseExchangeRate.class);
        return responseEntity.getBody().getRates().getEur();
    }

}
