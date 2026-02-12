package com.springlearning.kirana2.services;

import com.springlearning.kirana2.entity.enums.CurrencyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    private final RestClient restClient;

    public ExchangeRateService(
            @Value("${fxrates.api.base-url:https://api.fxratesapi.com/latest}") String baseUrl
    ) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("fxrates.api.base-url is missing or blank");
        }
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public BigDecimal convertToInr(BigDecimal amount, CurrencyType currency) {
        if (currency == CurrencyType.INR) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }
        if (currency == CurrencyType.USD) {
            BigDecimal rate = fetchUsdToInrRate();
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported currency: " + currency);
    }

    public BigDecimal convertFromInr(BigDecimal amountInInr, CurrencyType targetCurrency) {
        if (targetCurrency == CurrencyType.INR) {
            return amountInInr.setScale(2, RoundingMode.HALF_UP);
        }
        if (targetCurrency == CurrencyType.USD) {
            BigDecimal rate = fetchUsdToInrRate();
            if (rate.compareTo(BigDecimal.ZERO) == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Currency conversion rate unavailable");
            }
            return amountInInr.divide(rate, 2, RoundingMode.HALF_UP);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported currency: " + targetCurrency);
    }

    private BigDecimal fetchUsdToInrRate() {
        FxRatesResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("base", "USD")
                        .queryParam("symbols", "INR")
                        .build())
                .retrieve()
                .body(FxRatesResponse.class);

        if (response == null || response.getRates() == null || response.getRates().getINR() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Currency conversion rate unavailable");
        }
        return response.getRates().getINR();
    }

    public static class FxRatesResponse {
        private Rates rates;

        public Rates getRates() {
            return rates;
        }

        public void setRates(Rates rates) {
            this.rates = rates;
        }
    }

    public static class Rates {
        private BigDecimal INR;

        public BigDecimal getINR() {
            return INR;
        }

        public void setINR(BigDecimal INR) {
            this.INR = INR;
        }
    }
}
