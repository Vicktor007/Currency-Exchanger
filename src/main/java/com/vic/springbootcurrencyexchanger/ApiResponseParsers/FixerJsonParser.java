package com.vic.springbootcurrencyexchanger.ApiResponseParsers;

import com.vic.springbootcurrencyexchanger.Interfaces.JsonParser;
import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FixerJsonParser implements JsonParser {
    @Override
    public BigDecimal parseConversionRate(StringBuilder response) {
        return new JSONObject(response.toString()).getBigDecimal("result");
    }

    @Override
    public List<CurrencyRateHistory> parseConversionRateHistory(StringBuilder response, String base, String symbol) {
        return getCurrencyRateHistory(response,base,symbol);
    }

    static List<CurrencyRateHistory> getCurrencyRateHistory(StringBuilder response, String base, String symbol) {
        JSONObject rates = new JSONObject(response.toString()).getJSONObject("rates");
        List<CurrencyRateHistory> history = new ArrayList<>();
        for(String date : rates.keySet()) {
            BigDecimal currencyRate = rates.getJSONObject(date).getBigDecimal(symbol);
            history.add(new CurrencyRateHistory(base, symbol, date, currencyRate));
        }
        return history;
    }

    @Override
    public Map<String, String> parseSymbols(StringBuilder response) {
        JSONObject symbols = new JSONObject(response.toString()).getJSONObject("symbols");
        Map<String, String> symbolMap = new HashMap<>();
        for (String key : symbols.keySet()) {
            symbolMap.put(key, symbols.getString(key));
        }
        return symbolMap;
    }

    @Override
    public List<Currency> parseCurrencies(StringBuilder response) {
        Map<String, String> symbolMap = parseSymbols(response);
        return symbolMap.entrySet().stream()
                .map(entry -> new Currency(entry.getKey(), entry.getValue()))
                .toList();
    }
}
