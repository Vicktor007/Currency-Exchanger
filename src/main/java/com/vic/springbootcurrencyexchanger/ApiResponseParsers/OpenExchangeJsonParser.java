package com.vic.springbootcurrencyexchanger.ApiResponseParsers;

import com.vic.springbootcurrencyexchanger.Interfaces.JsonParser;
import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenExchangeJsonParser implements JsonParser {
    @Override
    public BigDecimal parseConversionRate(StringBuilder response) {
        JSONObject obj = new JSONObject(response.toString());
        return obj.getJSONObject("response").getBigDecimal("amount");
    }

    @Override
    public List<CurrencyRateHistory> parseConversionRateHistory(StringBuilder response, String base, String symbol) {
        return FixerJsonParser.getCurrencyRateHistory(response, base, symbol);
    }


    @Override
    public List<Currency> parseCurrencies(StringBuilder response) {
        Map<String, String> symbolMap = parseSymbols(response);
        return symbolMap.entrySet().stream()
                .map(entry -> new Currency(entry.getKey(), entry.getValue()))
                .toList();
    }


    private Map<String, String> parseSymbols(StringBuilder response) {
        JSONObject symbols = new JSONObject(response.toString());
        Map<String, String> symbolMap = new HashMap<>();
        for (String key : symbols.keySet()) {
            symbolMap.put(key, symbols.getString(key));
        }
        return symbolMap;
    }
}
