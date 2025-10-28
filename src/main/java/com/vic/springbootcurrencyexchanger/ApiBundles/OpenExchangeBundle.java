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
    private final JsonParser openExchangeParser;

    public OpenExchangeBundle(OpenExchangeApiConnection openProvider, @Qualifier("openExchangeJsonParser")JsonParser openExchangeParser) {
        this.openProvider = openProvider;
        this.openExchangeParser = openExchangeParser;
    }

    @Override
    public CurrencyApiProvider getProvider() {
        return openProvider;
    }

    @Override
    public JsonParser getJsonParser() {
        return openExchangeParser;
    }
}
