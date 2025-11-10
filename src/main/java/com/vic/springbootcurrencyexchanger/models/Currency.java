package com.vic.springbootcurrencyexchanger.models;

public class Currency {
    private String Symbol;
    private String Signification;

    public Currency(String symbol, String signification) {
        Symbol = symbol;
        Signification = signification;
    }

    public String getSymbol() {
        return Symbol;
    }



    public String getSignification() {
        return Signification;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "Symbol='" + Symbol + '\'' +
                ", Signification='" + Signification + '\'' +
                '}';
    }
}
