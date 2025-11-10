package com.vic.springbootcurrencyexchanger.ApiConnections;

import com.vic.springbootcurrencyexchanger.ApiResponseParsers.OpenExchangeJsonParser;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class OpenExchangeApiConnection implements CurrencyApiProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenExchangeApiConnection.class);
    private final RestTemplate restTemplate;
    private final OpenExchangeJsonParser openExchangeJsonParser;

    @Value("${open.api.url}")
    private String openApiUrl;
    @Value("${open.api.key}")
    private String openApiKey;

    public OpenExchangeApiConnection(RestTemplate restTemplate, OpenExchangeJsonParser openExchangeJsonParser) {
        this.restTemplate = restTemplate;
        this.openExchangeJsonParser = openExchangeJsonParser;
    }

    @Override
    public BigDecimal getRate(String from, String to, BigDecimal amount) {
        String url = String.format("%s/convert/%s/%s/%s?app_id=%s&prettyprint=false",
                openApiUrl, amount, from, to, openApiKey);

        return executeApiCall(
                () -> {
                    String response = restTemplate.getForObject(url, String.class);

                    if (response == null) {
                        throw new IOException("Empty response from OpenExchange API");
                    }
                    return openExchangeJsonParser.parseConversionRate(new StringBuilder(response));
                },
                "getRate",
                String.format("from=%s, to=%s, amount=%s", from, to, amount)
        );
    }

    @Override
    public List<CurrencyRateHistory> getCurrencyHistory(String base, LocalDate start, LocalDate end, String symbol) {
        String url = String.format("%s/time-series.json?app_id=%s&start=%s&end=%s&base=%s&symbols=%s&prettyprint=false",
                openApiUrl, openApiKey, start, end, base, symbol);

        return executeApiCall(
                () -> {
                    String response = restTemplate.getForObject(url, String.class);

                    if (response == null) {
                        throw new IOException("Empty response from OpenExchange API");
                    }

                    return openExchangeJsonParser.parseConversionRateHistory(new StringBuilder(response), base, symbol);
                },
                "getCurrencyHistory",
                String.format("base=%s, symbol=%s, period=%s to %s", base, symbol, start, end)
        );
    }

    @Override
    public List<Currency> getSymbolsWithSignification() {
        String url = String.format("%s/currencies.json?app_id=%s", openApiUrl, openApiKey);

        return executeApiCall(
                () -> {
                    String response = restTemplate.getForObject(url, String.class);

                    if (response == null) {
                        throw new IOException("Empty response from OpenExchange API");
                    }

                    return openExchangeJsonParser.parseCurrencies(new StringBuilder(response));
                },
                "getSymbolsWithSignification",
                ""
        );
    }

    private <T> T executeApiCall(ApiCallable<T> apiCall, String operation, String context) {
        try {
            log.debug("Executing {} with context: {}", operation, context);
            return apiCall.call();

        } catch (Exception e) {
            log.error("Failed to {} - Context: {} - Error: {}", operation, context, e.getMessage());
            return handleError(operation, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T handleError(String operation, Exception e) {

        return switch (operation) {
            case "getRate" -> {
                log.warn("Returning default rate due to API failure");
                yield (T) BigDecimal.ZERO;
            }
            case "getCurrencyHistory" -> {
                log.warn("Returning empty history list due to API failure");
                yield (T) Collections.emptyList();
            }
            case "getSymbolsWithSignification" -> {
                log.warn("Returning empty currencies list due to API failure");
                yield (T) Collections.emptyList();
            }
            default -> {
                log.error("Unhandled operation type: {}", operation);
                throw new RuntimeException("API operation failed: " + operation, e);
            }
        };
    }

    @FunctionalInterface
    private interface ApiCallable<T> {
        T call() throws Exception;
    }
}