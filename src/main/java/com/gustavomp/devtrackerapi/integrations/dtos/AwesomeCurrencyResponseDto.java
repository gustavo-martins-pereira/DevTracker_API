package com.gustavomp.devtrackerapi.integrations.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AwesomeCurrencyResponseDto(
        @JsonProperty("USDBRL")
        AwesomeCurrencyQuoteResponseDto usdBrl
) {}
