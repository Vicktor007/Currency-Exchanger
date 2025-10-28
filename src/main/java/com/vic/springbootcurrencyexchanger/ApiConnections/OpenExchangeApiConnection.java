package com.vic.springbootcurrencyexchanger.ApiConnections;

import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Objects;

@Service
public class OpenExchangeApiConnection implements CurrencyApiProvider {

    private final RestTemplate restTemplate;

    @Value("${open.api.url}")
    private String openApiUrl;
    @Value("${open.api.key}")
    private String openApiKey;

    public OpenExchangeApiConnection(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public StringBuilder getRate(String from, String to, BigDecimal amount) throws IOException, URISyntaxException {
        String url = openApiUrl + "/convert/" + amount + "/" + from + "/" + to +
                "?app_id=" + openApiKey + "&prettyprint=false";
        return new StringBuilder(Objects.requireNonNull(restTemplate.getForObject(url, String.class)));
    }

    @Override
    public StringBuilder getCurrencyHistory(String base, LocalDate start, LocalDate end, String symbol) throws IOException, URISyntaxException {
        String url = openApiUrl + "/time-series.json" +
                "?app_id=" + openApiKey +
                "&start=" + start +
                "&end=" + end +
                "&base=" + base +
                "&symbols=" + symbol +
                "&prettyprint=false";
        return new StringBuilder(Objects.requireNonNull(restTemplate.getForObject(url, String.class)));
    }

    @Override
    public StringBuilder getSymbolsWithSignification() throws IOException, URISyntaxException {
        String url = openApiUrl + "/currencies.json?app_id=" + openApiKey;
        return new StringBuilder(Objects.requireNonNull(restTemplate.getForObject(url, String.class)));
    }


}
