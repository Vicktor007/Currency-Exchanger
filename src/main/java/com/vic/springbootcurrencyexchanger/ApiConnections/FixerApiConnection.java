package com.vic.springbootcurrencyexchanger.ApiConnections;

import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;

@Service
public class FixerApiConnection implements CurrencyApiProvider {

    private final RestTemplate restTemplate;

    @Value("${fixer.api.url}")
    private String FixerApiUrl;
    @Value("${fixer.api.key}")
    private String FixerApiKey;

    public FixerApiConnection( RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public StringBuilder getRate(String from, String to, BigDecimal amount) throws IOException, URISyntaxException {
        String requestUrl = FixerApiUrl + "/convert?to=" + to + "&from=" + from + "&amount=" + amount;
        return doRequest(requestUrl);
    }

    @Override
    public StringBuilder getCurrencyHistory(String base, LocalDate startDate, LocalDate endDate, String symbol) throws IOException, URISyntaxException {
        String requestUrl = FixerApiUrl + "/timeseries?start_date=" + startDate +
                "&end_date=" + endDate +
                "&base=" + base +
                "&symbols=" + symbol;
        return doRequest(requestUrl);
    }

    @Override
    public StringBuilder getSymbolsWithSignification() throws IOException, URISyntaxException {
        String requestUrl = FixerApiUrl + "/symbols";
        return doRequest(requestUrl);
    }

    private StringBuilder doRequest(String requestUrl) throws IOException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", FixerApiKey);

        try{
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);
            assert response.getBody() != null;
            return new StringBuilder(response.getBody());

        }catch (HttpClientErrorException e) {
            throw new IllegalStateException("Connection failed: " + e.getStatusCode());
        }

    }
}
