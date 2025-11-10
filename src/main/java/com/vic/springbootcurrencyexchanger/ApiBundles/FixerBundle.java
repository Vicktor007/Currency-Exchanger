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

    public FixerBundle(FixerApiConnection fixerProvider) {
        this.fixerProvider = fixerProvider;
    }

    @Override
    public CurrencyApiProvider getProvider() {
        return fixerProvider;
    }

}
