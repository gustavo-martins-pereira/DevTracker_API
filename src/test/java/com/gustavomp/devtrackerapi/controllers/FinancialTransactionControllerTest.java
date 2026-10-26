package com.gustavomp.devtrackerapi.controllers;

import com.gustavomp.devtrackerapi.dtos.requests.financialtransaction.CreateFTRequestDto;
import com.gustavomp.devtrackerapi.dtos.responses.financialtransaction.*;
import com.gustavomp.devtrackerapi.exceptions.CurrencyProviderException;
import com.gustavomp.devtrackerapi.exceptions.GlobalExceptionHandler;
import com.gustavomp.devtrackerapi.models.enums.Currency;
import com.gustavomp.devtrackerapi.models.enums.TransactionType;
import com.gustavomp.devtrackerapi.services.financialtransaction.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Financial Transaction Controller")
public class FinancialTransactionControllerTest {

    @InjectMocks
    private FinancialTransactionController financialTransactionController;

    @Mock
    private CreateFTService createFTService;

    @Mock
    private GetAllFTByProjectIdService getAllFTByProjectIdService;

    @Mock
    private GetFTByIdService getFTByIdService;

    @Mock
    private DeleteFTByIdService deleteFTByIdService;

    private MockMvc mockMvc;

    private static final String PROJECT_NOT_FOUND_MESSAGE = "Project not found";
    private static final String FINANCIAL_TRANSACTION_NOT_FOUND_MESSAGE = "Financial transaction not found";
    private static final String CURRENCY_PROVIDER_MESSAGE = "Currency provider unavailable";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(financialTransactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    /* ---------- Create Financial Transaction ---------- */
    private static final String CREATE_JSON = """
            {
                "description": "Project payment",
                "amount": 100,
                "date": "2026-01-01",
                "transactionType": "INCOME",
                "projectId": 1
            }
            """;

    @ParameterizedTest
    @ValueSource(strings = {"INCOME", "EXPENSE"})
    @DisplayName("Create Financial Transaction | Valid request | Returns 201 and created transaction")
    void createFinancialTransaction_whenValidRequest_shouldReturnCreatedTransaction(String transactionType) throws Exception {
        // Arrange
        var type = TransactionType.valueOf(transactionType);
        var request = new CreateFTRequestDto(
                "Project payment",
                new BigDecimal("100"),
                LocalDate.of(2026, 1, 1),
                type,
                1L
        );
        var response = new CreateFTResponseDto(
                1L,
                "Project payment",
                new BigDecimal("100"),
                LocalDate.of(2026, 1, 1),
                type,
                1L
        );
        when(createFTService.execute(request)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/financial-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_JSON.replace("INCOME", transactionType)))
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "description": "Project payment",
                            "amount": 100,
                            "date": "2026-01-01",
                            "transactionType": "%s",
                            "projectId": 1
                        }
                        """.formatted(transactionType)));

        verify(createFTService).execute(request);
    }

    @Test
    @DisplayName("Create Financial Transaction | Project does not exist | Returns 404 Not Found")
    void createFinancialTransaction_whenProjectDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        var request = new CreateFTRequestDto(
                "Project payment",
                new BigDecimal("100"),
                LocalDate.of(2026, 1, 1),
                TransactionType.INCOME,
                1L
        );
        when(createFTService.execute(request)).thenThrow(new EntityNotFoundException(PROJECT_NOT_FOUND_MESSAGE));

        // Act & Assert
        mockMvc.perform(post("/financial-transactions").contentType(MediaType.APPLICATION_JSON).content(CREATE_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(PROJECT_NOT_FOUND_MESSAGE));

        verify(createFTService).execute(request);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"description\":\"\"}",
            "{\"description\":\"   \"}",
            "{\"description\":null}",
            "{\"amount\":null}",
            "{\"amount\":0}",
            "{\"amount\":-1}",
            "{\"amount\":100.123}",
            "{\"amount\":10000000000}",
            "{\"date\":null}",
            "{\"transactionType\":null}",
            "{\"projectId\":null}",
            "{\"projectId\":0}",
            "{\"projectId\":-1}"
    })
    @DisplayName("Create Financial Transaction | Invalid request | Returns 400 Bad Request")
    void createFinancialTransaction_whenInvalidRequest_shouldReturnBadRequest(String invalidField) throws Exception {
        // Arrange
        String field = invalidField.split(":")[0];
        field = field.replace("{", "").replace("\"", "");

        String replacement = invalidField.substring(1, invalidField.length() - 1);
        String fieldPattern = "\"" + field + "\":\\s*(\"[^\"]*\"|-?\\d+(\\.\\d+)?|null)";
        String invalidJson = CREATE_JSON.replaceFirst(fieldPattern, Matcher.quoteReplacement(replacement));
        assertNotEquals(CREATE_JSON, invalidJson, "The test must replace the selected field");

        // Act & Assert
        mockMvc.perform(post("/financial-transactions").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error." + field).isNotEmpty());

        verifyNoInteractions(createFTService);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{invalid",
            "{\"date\":\"invalid\"}",
            "{\"transactionType\":\"INVALID\"}",
            "{\"amount\":\"invalid\"}"
    })
    @DisplayName("Create Financial Transaction | Unreadable JSON | Returns 400 Bad Request")
    void createFinancialTransaction_whenUnreadableJson_shouldReturnBadRequest(String invalidJson) throws Exception {
        // Act & Assert
        mockMvc.perform(post("/financial-transactions").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").isMap());

        verifyNoInteractions(createFTService);
    }

    /* ---------- Get All Financial Transactions by Project Id ---------- */
    @Test
    @DisplayName("Get All Financial Transactions by Project Id | No currency specified | Returns 200 and transactions")
    void getAllFinancialTransactionByProjectId_whenCurrencyOmitted_shouldReturnTransactions() throws Exception {
        // Arrange
        var selectedCurrency = Currency.BRL;
        var response = new GetAllFTByProjectIdResponseDto(
                1L,
                "Project payment",
                new BigDecimal("100"),
                LocalDate.of(2026, 1, 1),
                TransactionType.INCOME
        );
        when(getAllFTByProjectIdService.execute(1L, selectedCurrency)).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/financial-transactions").param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{
                            "id":1,
                            "description":"Project payment",
                            "amount":100,
                            "date":"2026-01-01",
                            "transactionType":"INCOME"
                        }]
                        """));

        verify(getAllFTByProjectIdService).execute(1L, selectedCurrency);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BRL", "USD"})
    @DisplayName("Get All Financial Transactions by Project Id | Selected currency | Returns 200 and transactions")
    void getAllFinancialTransactionByProjectId_whenCurrencySpecified_shouldReturnTransactions(String currency) throws Exception {
        // Arrange
        var selectedCurrency = Currency.valueOf(currency);
        var response = new GetAllFTByProjectIdResponseDto(
                1L,
                "Project payment",
                new BigDecimal("100"),
                LocalDate.of(2026, 1, 1),
                TransactionType.INCOME
        );
        when(getAllFTByProjectIdService.execute(1L, selectedCurrency)).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/financial-transactions").param("projectId", "1").param("currency", currency))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{
                            "id":1,
                            "description":"Project payment",
                            "amount":100,
                            "date":"2026-01-01",
                            "transactionType":"INCOME"
                        }]
                        """));

        verify(getAllFTByProjectIdService).execute(1L, selectedCurrency);
    }

    @Test
    @DisplayName("Get All Financial Transactions by Project Id | No transactions exist | Returns 200 and empty list")
    void getAllFinancialTransactionByProjectId_whenNoTransactionsExist_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(getAllFTByProjectIdService.execute(1L, Currency.BRL)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/financial-transactions").param("projectId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(getAllFTByProjectIdService).execute(1L, Currency.BRL);
    }

    @Test
    @DisplayName("Get All Financial Transactions by Project Id | Invalid currency | Returns 400 Bad Request")
    void getAllFinancialTransactionByProjectId_whenInvalidCurrency_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/financial-transactions")
                        .param("projectId", "1").param("currency", "INVALID")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.currency").isNotEmpty());

        verifyNoInteractions(getAllFTByProjectIdService);
    }

    @Test
    @DisplayName("Get All Financial Transactions by Project Id | Invalid id | Returns 400 Bad Request")
    void getAllFinancialTransactionByProjectId_whenInvalidId_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/financial-transactions").param("projectId", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.projectId").isNotEmpty());

        verifyNoInteractions(getAllFTByProjectIdService);
    }

    @Test
    @DisplayName("Get All Financial Transactions by Project Id | Currency provider fails | Returns 502 Bad Gateway")
    void getAllFinancialTransactionByProjectId_whenCurrencyProviderFails_shouldReturnBadGateway() throws Exception {
        // Arrange
        when(getAllFTByProjectIdService.execute(1L, Currency.USD))
                .thenThrow(new CurrencyProviderException(CURRENCY_PROVIDER_MESSAGE));

        // Act & Assert
        mockMvc.perform(get("/financial-transactions")
                        .param("projectId", "1").param("currency", "USD")
                )
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value(CURRENCY_PROVIDER_MESSAGE));

        verify(getAllFTByProjectIdService).execute(1L, Currency.USD);
    }

    /* ---------- Get Financial Transaction by Id ---------- */
    @Test
    @DisplayName("Get Financial Transaction by Id | No currency specified | Returns 200 and transaction")
    void getFinancialTransactionById_whenCurrencyOmitted_shouldReturnTransaction() throws Exception {
        // Arrange
        var selectedCurrency = Currency.BRL;
        var response = new GetFTByIdResponseDto(
                1L,
                "Project payment",
                new BigDecimal("100"),
                LocalDate.of(2026, 1, 1),
                TransactionType.INCOME,
                1L,
                2L,
                "FIXED_PRICE"
        );
        when(getFTByIdService.execute(1L, selectedCurrency)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/financial-transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id":1,
                            "description":"Project payment",
                            "amount":100,
                            "date":"2026-01-01",
                            "transactionType":"INCOME",
                            "projectId":1,
                            "clientId":2,
                            "projectType":"FIXED_PRICE"
                        }
                        """));

        verify(getFTByIdService).execute(1L, selectedCurrency);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BRL", "USD"})
    @DisplayName("Get Financial Transaction by Id | Selected currency | Returns 200 and transaction")
    void getFinancialTransactionById_whenCurrencySpecified_shouldReturnTransaction(String currency) throws Exception {
        // Arrange
        var selectedCurrency = Currency.valueOf(currency);
        var response = new GetFTByIdResponseDto(
                1L,
                "Project payment",
                new BigDecimal("100"),
                LocalDate.of(2026, 1, 1),
                TransactionType.INCOME,
                1L,
                2L,
                "FIXED_PRICE"
        );
        when(getFTByIdService.execute(1L, selectedCurrency)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/financial-transactions/1").param("currency", currency))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id":1,
                            "description":"Project payment",
                            "amount":100,
                            "date":"2026-01-01",
                            "transactionType":"INCOME",
                            "projectId":1,
                            "clientId":2,
                            "projectType":"FIXED_PRICE"
                        }
                        """));

        verify(getFTByIdService).execute(1L, selectedCurrency);
    }

    @Test
    @DisplayName("Get Financial Transaction by Id | Transaction does not exist | Returns 404 Not Found")
    void getFinancialTransactionById_whenTransactionDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        when(getFTByIdService.execute(1L, Currency.BRL))
                .thenThrow(new EntityNotFoundException(FINANCIAL_TRANSACTION_NOT_FOUND_MESSAGE));

        // Act & Assert
        mockMvc.perform(get("/financial-transactions/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(FINANCIAL_TRANSACTION_NOT_FOUND_MESSAGE));

        verify(getFTByIdService).execute(1L, Currency.BRL);
    }

    @Test
    @DisplayName("Get Financial Transaction by Id | Invalid currency | Returns 400 Bad Request")
    void getFinancialTransactionById_whenInvalidCurrency_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/financial-transactions/1").param("currency", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.currency").isNotEmpty());

        verifyNoInteractions(getFTByIdService);
    }

    @Test
    @DisplayName("Get Financial Transaction by Id | Invalid id | Returns 400 Bad Request")
    void getFinancialTransactionById_whenInvalidId_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/financial-transactions/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.id").isNotEmpty());

        verifyNoInteractions(getFTByIdService);
    }

    @Test
    @DisplayName("Get Financial Transaction by Id | Currency provider fails | Returns 502 Bad Gateway")
    void getFinancialTransactionById_whenCurrencyProviderFails_shouldReturnBadGateway() throws Exception {
        // Arrange
        when(getFTByIdService.execute(1L, Currency.USD))
                .thenThrow(new CurrencyProviderException(CURRENCY_PROVIDER_MESSAGE));

        // Act & Assert
        mockMvc.perform(get("/financial-transactions/1").param("currency", "USD"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.error").value(CURRENCY_PROVIDER_MESSAGE));

        verify(getFTByIdService).execute(1L, Currency.USD);
    }

    /* ---------- Delete Financial Transaction by Id ---------- */
    @Test
    @DisplayName("Delete Financial Transaction by Id | Transaction exists | Returns 204 No Content")
    void deleteFinancialTransactionById_whenTransactionExists_shouldReturnNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/financial-transactions/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(deleteFTByIdService).execute(1L);
    }

    @Test
    @DisplayName("Delete Financial Transaction by Id | Transaction does not exist | Returns 404 Not Found")
    void deleteFinancialTransactionById_whenTransactionDoesNotExist_shouldReturnNotFound() throws Exception {
        // Arrange
        doThrow(new EntityNotFoundException(FINANCIAL_TRANSACTION_NOT_FOUND_MESSAGE))
                .when(deleteFTByIdService).execute(1L);

        // Act & Assert
        mockMvc.perform(delete("/financial-transactions/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(FINANCIAL_TRANSACTION_NOT_FOUND_MESSAGE));

        verify(deleteFTByIdService).execute(1L);
    }

    @Test
    @DisplayName("Delete Financial Transaction by Id | Invalid id | Returns 400 Bad Request")
    void deleteFinancialTransactionById_whenInvalidId_shouldReturnBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/financial-transactions/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.id").isNotEmpty());

        verifyNoInteractions(deleteFTByIdService);
    }
}

