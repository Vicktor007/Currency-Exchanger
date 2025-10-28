package com.vic.springbootcurrencyexchanger.ApiBundles;

import com.vic.springbootcurrencyexchanger.ApiConnections.FixerApiConnection;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyProviderBundle;
import com.vic.springbootcurrencyexchanger.Interfaces.JsonParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fixerBundle")
public class FixerBundle implements CurrencyProviderBundle {
    private final FixerApiConnection fixerProvider;
    private final JsonParser fixerParser;

    public FixerBundle(FixerApiConnection fixerProvider, @Qualifier("fixerJsonParser") JsonParser fixerParser) {
        this.fixerProvider = fixerProvider;
        this.fixerParser = fixerParser;
    }

    @Override
    public CurrencyApiProvider getProvider() {
        return fixerProvider;
    }

    @Override
    public JsonParser getJsonParser() {
        return fixerParser;
    }
}
