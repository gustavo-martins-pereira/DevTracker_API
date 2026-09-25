package com.gustavomp.devtrackerapi.integrations;

import com.gustavomp.devtrackerapi.exceptions.CurrencyProviderException;
import com.gustavomp.devtrackerapi.integrations.dtos.AwesomeCurrencyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class AwesomeApiClient {

    private final RestClient awesomeApiRestClient;

    public AwesomeCurrencyResponseDto getUsdBrlQuote() {
        try {
            return awesomeApiRestClient
                    .get()
                    .uri("/json/last/USD-BRL")
                    .retrieve()
                    .body(AwesomeCurrencyResponseDto.class);
        } catch (RestClientException ex) {
            throw new CurrencyProviderException("Unable to retrieve the USD/BRL exchange rate", ex);
        }
    }

}
