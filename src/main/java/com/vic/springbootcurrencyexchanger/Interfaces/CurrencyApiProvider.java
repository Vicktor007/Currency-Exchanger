package com.vic.springbootcurrencyexchanger.Interfaces;


import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

public interface CurrencyApiProvider {

    BigDecimal getRate(String from, String to, BigDecimal amount) throws IOException, URISyntaxException;
    List<CurrencyRateHistory> getCurrencyHistory(String base, LocalDate start, LocalDate end, String symbol) throws IOException, URISyntaxException;
    List<Currency> getSymbolsWithSignification() throws IOException, URISyntaxException;



}
