package com.karpuk.account.emulator.upstream.currency.client;

import com.karpuk.account.emulator.upstream.currency.model.ResponseExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyExchangeClient {

    public CurrencyExchangeClient() {
    }

    @Autowired
    private RestTemplate restTemplate;

    private static final String URL = "https://api.exchangeratesapi.io/latest";
    private static final String BASE_VALUE = "USD";

    public double getUsdEuroRate() {
        ResponseEntity<ResponseExchangeRate> responseEntity = restTemplate.getForEntity(URL + "?base=" + BASE_VALUE,
                ResponseExchangeRate.class);
        return responseEntity.getBody().getRates().getEur();
    }

}
