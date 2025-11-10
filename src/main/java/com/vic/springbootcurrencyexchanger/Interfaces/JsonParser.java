package com.vic.springbootcurrencyexchanger.Interfaces;

import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface JsonParser {
    BigDecimal parseConversionRate(StringBuilder response);
    List<CurrencyRateHistory> parseConversionRateHistory(StringBuilder response, String base, String symbol);
    List<Currency> parseCurrencies(StringBuilder response);

}
