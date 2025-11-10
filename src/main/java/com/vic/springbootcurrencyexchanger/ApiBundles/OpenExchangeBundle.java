package com.vic.springbootcurrencyexchanger.ApiBundles;

import com.vic.springbootcurrencyexchanger.ApiConnections.OpenExchangeApiConnection;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyApiProvider;
import com.vic.springbootcurrencyexchanger.Interfaces.CurrencyProviderBundle;
import com.vic.springbootcurrencyexchanger.Interfaces.JsonParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("openExchangeBundle")
public class OpenExchangeBundle implements CurrencyProviderBundle {

    private final OpenExchangeApiConnection openProvider;

    public OpenExchangeBundle(OpenExchangeApiConnection openProvider) {
        this.openProvider = openProvider;
    }

    @Override
    public CurrencyApiProvider getProvider() {
        return openProvider;
    }

}
