package com.gustavomp.devtrackerapi.integrations.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AwesomeCurrencyQuoteResponseDto(
        String code,

        @JsonProperty("codein")
        String codeIn,

        String bid
) {}
