package com.vic.springbootcurrencyexchanger;

import com.vic.springbootcurrencyexchanger.models.CurrencyRates;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRateRepository extends JpaRepository<CurrencyRates, Long> {
}
