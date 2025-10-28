package com.vic.springbootcurrencyexchanger.Services;

import com.vic.springbootcurrencyexchanger.ApiBundles.FixerBundle;
import com.vic.springbootcurrencyexchanger.ApiBundles.OpenExchangeBundle;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyProviderBundle;
import com.vic.springbootcurrencyexchanger.Interfaces.JsonParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;

@Service
public class ApiProviderSwitch implements CurrencyApiProvider {

    private final CurrencyProviderBundle primaryBundle;
    private final CurrencyProviderBundle secondaryBundle;

    private final CurrencyProviderBundle activeBundle;

    public ApiProviderSwitch(FixerBundle fixerBundle, OpenExchangeBundle openExchangeBundle) {
        this.primaryBundle = fixerBundle;
        this.secondaryBundle = openExchangeBundle;
        this.activeBundle = fixerBundle; //default to fixer
    }

    public JsonParser getActiveParser() {
        return activeBundle.getJsonParser();
    }

    private StringBuilder ApiCallorSwitch(ApiCall call) throws IOException, URISyntaxException {
        int retries = 3;
        int delay = 1000; // 1s
        for(int i = 0; i < retries; i++){

        try{
            return call.call(primaryBundle.getProvider());
        } catch (Exception e) {
            System.out.println("Retry " + (i + 1) + " failed: " + e.getMessage());
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {}
            delay *= 2;
        }}
        System.out.println("Fixer api unavailable, switching to open exchange...");
        return call.call(secondaryBundle.getProvider());
    }


    @Override
    public StringBuilder getRate(String from, String to, BigDecimal amount) throws IOException, URISyntaxException {
        return ApiCallorSwitch(provider -> provider.getRate(from, to, amount));
    }

    @Override
    public StringBuilder getCurrencyHistory(String base, LocalDate start, LocalDate end, String symbol) throws IOException, URISyntaxException {
        return ApiCallorSwitch(provider -> provider.getCurrencyHistory(base, start, end, symbol));
    }

    @Override
    public StringBuilder getSymbolsWithSignification() throws IOException, URISyntaxException {
        return ApiCallorSwitch(CurrencyApiProvider::getSymbolsWithSignification);
    }

    @FunctionalInterface
    private interface ApiCall {
        StringBuilder call(CurrencyApiProvider provider) throws IOException, URISyntaxException;
    }
}
