package com.vic.springbootcurrencyexchanger.Interfaces;

public interface CurrencyProviderBundle {
    CurrencyApiProvider getProvider();
    JsonParser getJsonParser();
}
