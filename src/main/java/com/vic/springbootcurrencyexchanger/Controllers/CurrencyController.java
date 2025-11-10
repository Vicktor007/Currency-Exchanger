package com.vic.springbootcurrencyexchanger.Controllers;

import com.vic.springbootcurrencyexchanger.Services.CurrencyDataConverterService;
import com.vic.springbootcurrencyexchanger.models.Currency;
import com.vic.springbootcurrencyexchanger.models.CurrencyRateHistory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/currency")
@Tag(name = "Currency API", description = "Currency conversion and historical data operations")
public class CurrencyController {

    private final CurrencyDataConverterService converterService;

    public CurrencyController(CurrencyDataConverterService converterService) {
        this.converterService = converterService;
    }

    @Operation(
            summary = "Convert currency",
            description = "Convert an amount from one currency to another using current exchange rates"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful conversion",
                    content = @Content(schema = @Schema(implementation = BigDecimal.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid currency code or amount"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error or external API unavailable"
            )
    })
    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convert(
            @Parameter(
                    description = "Source currency code (ISO 4217)",
                    example = "USD",
                    required = true
            )
            @RequestParam String from,

            @Parameter(
                    description = "Target currency code (ISO 4217)",
                    example = "EUR",
                    required = true
            )
            @RequestParam String to,

            @Parameter(
                    description = "Amount to convert",
                    example = "100.00",
                    required = true
            )
            @RequestParam BigDecimal amount
    ) {
        return ResponseEntity.ok(converterService.convert(from, to, amount));
    }

    @Operation(
            summary = "Get all supported currencies",
            description = "Retrieve list of all available currencies with their codes and names"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of all supported currencies",
            content = @Content(schema = @Schema(implementation = Currency[].class))
    )
    @GetMapping("/all")
    public ResponseEntity<List<Currency>> getAllCurrencies() {
        return ResponseEntity.ok(converterService.getAllCurrencies());
    }

    @Operation(
            summary = "Get currency names and symbols",
            description = "Get formatted list of all currency names with their symbols in sorted order"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of formatted currency names and symbols",
            content = @Content(schema = @Schema(implementation = String[].class))
    )
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllCurrencyNamesAndSymbols() {
        List<Currency> currencies = converterService.getAllCurrencies();
        return ResponseEntity.ok(converterService.getAllCurrencyNamesAndSignifications(currencies));
    }

    @Operation(
            summary = "Get historical exchange rates",
            description = "Retrieve historical exchange rates for specified currency pair over last X days"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of historical rates",
                    content = @Content(schema = @Schema(implementation = CurrencyRateHistory[].class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid currency code or days parameter"
            )
    })
    @GetMapping("/history")
    public ResponseEntity<List<CurrencyRateHistory>> getHistory(
            @Parameter(
                    description = "Base currency code",
                    example = "USD",
                    required = true
            )
            @RequestParam String base,

            @Parameter(
                    description = "Target currency code",
                    example = "EUR",
                    required = true
            )
            @RequestParam String to,

            @Parameter(
                    description = "Number of days of history to retrieve (default: 1, max: 365)",
                    example = "7"
            )
            @RequestParam(defaultValue = "1") Integer days
    ) {
        return ResponseEntity.ok(converterService.getCurrencyRateHistory(base, days, to));
    }

    @Operation(
            summary = "Find currency by keyword",
            description = "Search for currencies by code, name, or symbol using case-insensitive matching"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of matching currencies",
                    content = @Content(schema = @Schema(implementation = Currency[].class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No currencies found matching the keyword"
            )
    })
    @GetMapping("/findCurrency/{keyWord}")
    public ResponseEntity<List<Currency>> findCurrency(
            @Parameter(
                    description = "Search keyword (currency code, name, or symbol)",
                    example = "USD",
                    required = true
            )
            @PathVariable String keyWord
    ) {
        return ResponseEntity.ok(converterService.findCurrency(keyWord));
    }
}