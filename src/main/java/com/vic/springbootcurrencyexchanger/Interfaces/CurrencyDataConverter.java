package com.vic.springbootcurrencyexchanger.Interfaces;

import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyDataConverter {
    BigDecimal convert(String fromCurrency, String toCurrency, BigDecimal value);
    List<Currency> getAllCurrencies();
    List<String> getAllCurrencyNamesAndSignifications(List<Currency> currencies);
    List<CurrencyRateHistory> getCurrencyRateHistory(String baseCurrency, Integer duration, String toCurrency);
    List<Currency> findCurrency(String keyword);
}
