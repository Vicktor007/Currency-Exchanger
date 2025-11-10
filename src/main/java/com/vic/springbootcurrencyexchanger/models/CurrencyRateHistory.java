package com.vic.springbootcurrencyexchanger.models;

import java.math.BigDecimal;

public class CurrencyRateHistory {
    private String base;
    private String symbol;
    private String day;
    private BigDecimal value;

    public CurrencyRateHistory(String base, String symbol, String day, BigDecimal value) {
        this.base = base;
        this.symbol = symbol;
        this.day = day;
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String getDay() {
        return day;
    }
}
