package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.services.awesomeapi.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/awesomeapi/currency")
@RequiredArgsConstructor
public class AwesomeApiController {

    private final CurrencyService currencyService;

    @GetMapping("/usd-brl")
    public BigDecimal getUsdBrlExchangeRate() {
        return currencyService.getUsdBrlExchangeRate();
    }

}
