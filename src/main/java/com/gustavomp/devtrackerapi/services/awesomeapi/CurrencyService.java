package com.gustavomp.devtrackerapi.services.awesomeapi;

import com.gustavomp.devtrackerapi.exceptions.CurrencyProviderException;
import com.gustavomp.devtrackerapi.integrations.AwesomeApiClient;
import com.gustavomp.devtrackerapi.integrations.dtos.AwesomeCurrencyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final AwesomeApiClient awesomeApiClient;

    public BigDecimal getUsdBrlExchangeRate() {
        AwesomeCurrencyResponseDto response = awesomeApiClient.getUsdBrlQuote();

        if (response == null || response.usdBrl() == null
                || !"USD".equals(response.usdBrl().code())
                || !"BRL".equals(response.usdBrl().codeIn())
                || response.usdBrl().bid() == null) {
            throw new CurrencyProviderException("Invalid USD/BRL quote received from AwesomeAPI");
        }

        try {
            BigDecimal rate = new BigDecimal(response.usdBrl().bid());

            if (rate.signum() <= 0) {
                throw new CurrencyProviderException("Invalid USD/BRL exchange rate received from AwesomeAPI");
            }

            return rate;
        } catch (NumberFormatException ex) {
            throw new CurrencyProviderException("Invalid USD/BRL exchange rate received from AwesomeAPI", ex);
        }
    }
}
