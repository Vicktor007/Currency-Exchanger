package com.vic.springbootcurrencyexchanger.Services;

import com.vic.springbootcurrencyexchanger.ApiBundles.FixerBundle;
import com.vic.springbootcurrencyexchanger.ApiBundles.OpenExchangeBundle;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyProviderBundle;
import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;


@Service
public class ApiProviderSwitch implements CurrencyApiProvider {

    private static final Logger log = LoggerFactory.getLogger(ApiProviderSwitch.class);
    private final CurrencyProviderBundle primaryBundle;
    private final CurrencyProviderBundle secondaryBundle;


    @Value("${apiCallDelay}")
    private int apiCallDelay;

    @Value("${apiCallRetries}")
    private int apiCallRetries;

    public ApiProviderSwitch(FixerBundle fixerBundle, OpenExchangeBundle openExchangeBundle) {
        this.primaryBundle = fixerBundle;
        this.secondaryBundle = openExchangeBundle;
    }



    private <T> T  ApiCallorSwitch(ApiCall<T> call) throws IOException, URISyntaxException {
        int retries = apiCallRetries;
        int delay = apiCallDelay;
        for(int i = 0; i < retries; i++){

        try{
            return call.call(primaryBundle.getProvider());
        } catch (Exception e) {
            log.error("Retry {} failed: {}", i + 1, e.getMessage());
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {}
            delay *= 2;
        }}
        System.out.println("Fixer api unavailable, switching to open exchange...");
        return call.call(secondaryBundle.getProvider());
    }


    @Override
    public BigDecimal getRate(String from, String to, BigDecimal amount) throws IOException, URISyntaxException {
        return ApiCallorSwitch(provider -> provider.getRate(from, to, amount));
    }

    @Override
    public List<CurrencyRateHistory> getCurrencyHistory(String base, LocalDate start, LocalDate end, String symbol) throws IOException, URISyntaxException {
        return ApiCallorSwitch(provider -> provider.getCurrencyHistory(base, start, end, symbol));
    }

    @Override
    public List<Currency> getSymbolsWithSignification() throws IOException, URISyntaxException {
        return ApiCallorSwitch(CurrencyApiProvider::getSymbolsWithSignification);
    }

    @FunctionalInterface
    private interface ApiCall<T> {
        T call(CurrencyApiProvider provider) throws IOException, URISyntaxException;
    }
}
