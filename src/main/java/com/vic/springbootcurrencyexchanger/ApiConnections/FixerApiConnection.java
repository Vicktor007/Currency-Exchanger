package com.vic.springbootcurrencyexchanger.ApiConnections;

import com.vic.springbootcurrencyexchanger.ApiResponseParsers.FixerJsonParser;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

@Service
public class FixerApiConnection implements CurrencyApiProvider {

    private static final Logger log = LoggerFactory.getLogger(FixerApiConnection.class);
    private final RestTemplate restTemplate;
    private final FixerJsonParser fixerJsonParser;

    @Value("${fixer.api.url}")
    private String FixerApiUrl;
    @Value("${fixer.api.key}")
    private String FixerApiKey;

    public FixerApiConnection(RestTemplate restTemplate, FixerJsonParser fixerJsonParser) {
        this.restTemplate = restTemplate;
        this.fixerJsonParser = fixerJsonParser;
    }

    @Override
    public BigDecimal getRate(String from, String to, BigDecimal amount) throws IOException, URISyntaxException {
        String requestUrl = FixerApiUrl + "/convert?to=" + to + "&from=" + from + "&amount=" + amount;
        StringBuilder response = doRequest(requestUrl);
        return fixerJsonParser.parseConversionRate(response);
    }

    @Override
    public List<CurrencyRateHistory> getCurrencyHistory(String base, LocalDate startDate, LocalDate endDate, String symbol) throws IOException, URISyntaxException {
        String requestUrl = FixerApiUrl + "/timeseries?start_date=" + startDate +
                "&end_date=" + endDate +
                "&base=" + base +
                "&symbols=" + symbol;
        StringBuilder response = doRequest(requestUrl);
        return fixerJsonParser.parseConversionRateHistory(new StringBuilder(response), base, symbol);
    }

    @Override
    public List<Currency> getSymbolsWithSignification() throws IOException {
        String requestUrl = FixerApiUrl + "/symbols";
        StringBuilder response = doRequest(requestUrl);
        return fixerJsonParser.parseCurrencies(new StringBuilder(response));
    }

    private StringBuilder doRequest(String requestUrl) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", FixerApiKey);

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);

            log.debug("Fixer API Request: {}", requestUrl);
            log.debug("Fixer API Response Status: {}", response.getStatusCode());

            if (response.getBody() == null) {
                throw new IOException("Empty response body from Fixer API");
            }

            log.debug("Fixer API Response: {}", response.getBody());
            return new StringBuilder(response.getBody());

        } catch (Exception e) {
            String errorMessage = String.format("Fixer API request failed for URL: %s - Error: %s", requestUrl, e.getMessage());
            log.error(errorMessage);
            throw new IOException(errorMessage, e);
        }
    }
}