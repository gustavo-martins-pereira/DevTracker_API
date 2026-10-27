package com.gustavomp.devtrackerapi.integrations.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AwesomeCurrencyQuoteResponseDto(
        String code,

        @JsonProperty("codeIn")
        String codeIn,

        String bid
) {}
