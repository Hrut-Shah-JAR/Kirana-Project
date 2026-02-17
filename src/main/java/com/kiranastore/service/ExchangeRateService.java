package com.kiranastore.service;

import com.kiranastore.entity.enums.CurrencyType;
import com.kiranastore.exception.BadRequestException;
import com.kiranastore.exception.UpstreamServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {

    private final RestClient restClient;

    /**
     * Creates the exchange rate service using the configured RestClient bean.
     *
     * @param restClient configured exchange rate RestClient
     */
    public ExchangeRateService(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Converts the given amount from the specified currency to INR.
     *
     * @param amount amount in source currency
     * @param currency source currency
     * @return amount in INR
     */
    public BigDecimal convertToInr(BigDecimal amount, CurrencyType currency) {
        if (currency == CurrencyType.INR) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }
        if (currency == CurrencyType.USD) {
            BigDecimal rate = fetchUsdToInrRate();
            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }
        throw new BadRequestException("Unsupported currency: " + currency);
    }


    /**
     * Converts the given INR amount to the target currency.
     *
     * @param amountInInr amount in INR
     * @param targetCurrency target currency
     * @return amount in target currency
     */
    public BigDecimal convertFromInr(BigDecimal amountInInr, CurrencyType targetCurrency) {
        if (targetCurrency == CurrencyType.INR) {
            return amountInInr.setScale(2, RoundingMode.HALF_UP);
        }
        if (targetCurrency == CurrencyType.USD) {
            BigDecimal rate = fetchUsdToInrRate();
            if (rate.compareTo(BigDecimal.ZERO) == 0) {
                throw new UpstreamServiceException("Currency conversion rate unavailable");
            }
            return amountInInr.divide(rate, 2, RoundingMode.HALF_UP);
        }
        throw new BadRequestException("Unsupported currency: " + targetCurrency);
    }

    /**
     * Fetches the USD to INR rate from the external API.
     *
     * @return USD to INR conversion rate
     */
    private BigDecimal fetchUsdToInrRate() {
        FxRatesResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("base", "USD")
                        .queryParam("symbols", "INR")
                        .build())
                .retrieve()
                .body(FxRatesResponse.class);

        if (response == null || response.getRates() == null || response.getRates().getINR() == null) {
            throw new UpstreamServiceException("Currency conversion rate unavailable");
        }
        return response.getRates().getINR();
    }

    public static class FxRatesResponse {
        private Rates rates;

        /**
         * Returns the rates container.
         *
         * @return rates
         */
        public Rates getRates() {
            return rates;
        }

        /**
         * Sets the rates container.
         *
         * @param rates rates container
         */
        public void setRates(Rates rates) {
            this.rates = rates;
        }
    }

    public static class Rates {
        private BigDecimal INR;

        /**
         * Returns the INR rate.
         *
         * @return INR rate
         */
        public BigDecimal getINR() {
            return INR;
        }

        /**
         * Sets the INR rate.
         *
         * @param INR INR rate
         */
        public void setINR(BigDecimal INR) {
            this.INR = INR;
        }
    }
}
