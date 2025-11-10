package com.vic.springbootcurrencyexchanger.Configuratons;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        CaffeineCache conversionCache = new CaffeineCache(
                "conversion",
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build()
        );

        CaffeineCache historyCache = new CaffeineCache(
                "history",
                Caffeine.newBuilder()
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .maximumSize(100)
                        .build()
        );

        CaffeineCache allCurrenciesCache = new CaffeineCache(
                "allCurrencies",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .build()
        );

        CaffeineCache allCurrenciesSignificationsCache = new CaffeineCache(
                "allCurrenciesAndSignifications",
                Caffeine.newBuilder()
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .build()
        );

        cacheManager.setCaches(Arrays.asList(
                conversionCache,
                historyCache,
                allCurrenciesCache,
                allCurrenciesSignificationsCache
        ));

        return cacheManager;
    }
}
