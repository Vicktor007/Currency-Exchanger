package com.vic.springbootcurrencyexchanger.Services;

import com.vic.springbootcurrencyexchanger.CurrencyRateRepository;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyDataConverter;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyProviderBundle;
import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;
import com.vic.springbootcurrencyexchanger.models.CurrencyRates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyDataConverterService implements CurrencyDataConverter {

    private final ApiProviderSwitch apiProvider;
    private final CurrencyDataConverterService self; //proxy reference to self to ensure cached data is used
    private final CurrencyRateRepository rateRepository;
    private final CurrencyProviderBundle primaryBundle;
    private final CurrencyProviderBundle secondaryBundle;
    private final Logger log = LoggerFactory.getLogger(CurrencyDataConverterService.class);


    public CurrencyDataConverterService(ApiProviderSwitch apiProvider, @Lazy CurrencyDataConverterService self, CurrencyRateRepository rateRepository, @Qualifier("fixerBundle") CurrencyProviderBundle primaryBundle, @Qualifier("openExchangeBundle") CurrencyProviderBundle secondaryBundle) {
        this.apiProvider = apiProvider;
        this.self = self;
        this.rateRepository = rateRepository;
        this.primaryBundle = primaryBundle;
        this.secondaryBundle = secondaryBundle;
    }


    @Override
    @Cacheable(value = "conversion", key = "#fromCurrency + '_' + #toCurrency + '_' + #value")
    public BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal value) {
        List<BigDecimal> results = new ArrayList<>();

        try {
            BigDecimal result1 = primaryBundle.getProvider().getRate(fromCurrency, toCurrency, value);
            results.add(result1);
            log.info("Primary API: {}", result1);
        } catch (Exception e) {
            log.error("Primary API unavailable: {}", e.getMessage());
        }

        try {
            BigDecimal result2 = secondaryBundle.getProvider().getRate(fromCurrency, toCurrency, value);
            results.add(result2);
            log.info("Secondary API: {}", result2);
        } catch (Exception e) {
            log.error("Secondary API unavailable: {}", e.getMessage());
        }

        if (results.isEmpty()) {
            log.error("All currency conversion APIs are unavailable");
            throw new RuntimeException("All currency conversion APIs are unavailable");
        }

        // Check if all results are zero and stop operation
        boolean allZero = results.stream()
                .allMatch(result -> result.compareTo(BigDecimal.ZERO) == 0);

        if (allZero) {
            log.error("All APIs returned zero - currency conversion failed for {}/{}", fromCurrency, toCurrency);
            throw new RuntimeException("Currency conversion failed - all APIs returned zero");
        }

        BigDecimal finalResult;

        if (results.size() == 2 &&
                results.get(0).compareTo(BigDecimal.ZERO) != 0 &&
                results.get(1).compareTo(BigDecimal.ZERO) != 0) {
            finalResult = results.get(0).add(results.get(1))
                    .divide(new BigDecimal("2"), 6, RoundingMode.HALF_UP);
            log.info("Using average of {} APIs: {}", results.size(), finalResult);
        } else {
            finalResult = results.stream()
                    .filter(result -> result.compareTo(BigDecimal.ZERO) != 0)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Unexpected error: No valid results found"));
                log.info("Using single valid API result: {}", finalResult);
            }

        BigDecimal rate = value.divide(finalResult, 6, RoundingMode.HALF_UP);

        CurrencyRates newCurrencyRates = new CurrencyRates();
        newCurrencyRates.setFromCurrency(fromCurrency);
        newCurrencyRates.setToCurrency(toCurrency);
        newCurrencyRates.setAmountConvertedFrom(value);
        newCurrencyRates.setAmountConvertedTo(finalResult);
        newCurrencyRates.setRate(rate);
        rateRepository.save(newCurrencyRates);

        return finalResult.setScale(2, RoundingMode.HALF_UP);

    }

    @Override
    @Cacheable(value = "allCurrencies")
    public List<Currency> getAllCurrencies() {
        try{
            return apiProvider.getSymbolsWithSignification();
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to fetch currencies: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch currencies: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(value = "allCurrenciesAndSignifications")
    public List<String> getAllCurrencyNamesAndSignifications(List<Currency> currencies) {
        List<String> allCurrencies;

        try {
          allCurrencies = currencies.stream()
                    .sorted(Comparator.comparing(Currency::getSignification))
                    .map(currency -> currency.getSignification() + " " + currency.getSymbol())
                    .toList();
        } catch (Exception e) {
            log.error("Failed to fetch currencies: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch currencies: " + e.getMessage());
        }
        return allCurrencies;
    }



    @Override
    @Cacheable(value = "history", key = "#baseCurrency + '_' + #duration + '_' + #toCurrency ")
    public List<CurrencyRateHistory> getCurrencyRateHistory(String baseCurrency, Integer duration, String toCurrency) {
        try {
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusDays(duration);
            return apiProvider.getCurrencyHistory(baseCurrency, startDate, today, toCurrency );
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to fetch currency history: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Currency> findCurrency( String keyword) {
        List<Currency> currencies = self.getAllCurrencies();
        if (keyword == null || keyword.isBlank()) {

            return currencies;
        }
        try {
            String search = keyword.toUpperCase();
            currencies = currencies.stream()
                    .filter(currency -> currency.getSymbol().contains(search) ||
                            currency.getSignification().toUpperCase().contains(search))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to find currency: {}", e.getMessage());
        }
        return currencies;
        }
}
