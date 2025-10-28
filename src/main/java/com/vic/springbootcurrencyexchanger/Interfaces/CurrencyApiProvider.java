package com.vic.springbootcurrencyexchanger.Interfaces;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;

public interface CurrencyApiProvider {

    StringBuilder getRate(String from, String to, BigDecimal amount) throws IOException, URISyntaxException;
    StringBuilder getCurrencyHistory(String base, LocalDate start, LocalDate end, String symbol) throws IOException, URISyntaxException;
    StringBuilder getSymbolsWithSignification() throws IOException, URISyntaxException;



}
