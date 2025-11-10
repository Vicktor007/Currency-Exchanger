package com.vic.springbootcurrencyexchanger.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;


@Entity
public class CurrencyRates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String fromCurrency;

    private String toCurrency;

    private BigDecimal rate;

    private BigDecimal amountConvertedFrom;

    private BigDecimal amountConvertedTo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmountConvertedFrom() {
        return amountConvertedFrom;
    }

    public void setAmountConvertedFrom(BigDecimal amountConvertedFrom) {
        this.amountConvertedFrom = amountConvertedFrom;
    }

    public BigDecimal getAmountConvertedTo() {
        return amountConvertedTo;
    }

    public void setAmountConvertedTo(BigDecimal amountConvertedTo) {
        this.amountConvertedTo = amountConvertedTo;
    }
}
