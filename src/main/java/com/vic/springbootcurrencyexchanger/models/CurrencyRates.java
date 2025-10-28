package com.vic.springbootcurrencyexchanger.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class CurrencyRates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String fromCurrency;

    private String toCurrency;

    private String rate;

    private String amountConvertedFrom;

    private String amountConvertedTo;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setAmountConvertedFrom(String amountConvertedFrom) {
        this.amountConvertedFrom = amountConvertedFrom;
    }

    public void setAmountConvertedTo(String amountConvertedTo) {
        this.amountConvertedTo = amountConvertedTo;
    }
}
