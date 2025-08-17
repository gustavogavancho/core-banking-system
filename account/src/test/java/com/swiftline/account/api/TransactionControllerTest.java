package com.swiftline.account.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swiftline.account.application.dto.TransactionRequest;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.application.service.TransactionService;
import com.swiftline.account.domain.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest {

    private MockMvc mockMvc;
    private TransactionService transactionService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        transactionService = Mockito.mock(TransactionService.class);
        TransactionController controller = new TransactionController(transactionService, new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Configurar ObjectMapper para manejar LocalDateTime de manera más explícita
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void create_shouldReturn201() throws Exception {
        Long accountId = 10L;
        String jsonRequest = """
            {
                "date": "2024-01-15T10:30:00",
                "transactionType": "DEPOSIT",
                "amount": 50.00,
                "balance": 150.00
            }
            """;
        Transaction created = transactionWithId(1L, accountId);
        when(transactionService.create(eq(accountId), any(TransactionRequest.class))).thenReturn(created);

        mockMvc.perform(post("/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/transactions/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountId", is(accountId.intValue())))
                .andExpect(jsonPath("$.transactionType", is("DEPOSIT")));
    }

    @Test
    void create_shouldReturn404_whenAccountMissing() throws Exception {
        Long accountId = 99L;
        String jsonRequest = """
            {
                "date": "2024-01-15T10:30:00",
                "transactionType": "DEPOSIT",
                "amount": 50.00,
                "balance": 150.00
            }
            """;
        when(transactionService.create(eq(accountId), any(TransactionRequest.class)))
                .thenThrow(new NotFoundException("no account"));

        mockMvc.perform(post("/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("no account")));
    }

    @Test
    void get_shouldReturn200_whenFound() throws Exception {
        when(transactionService.get(5L)).thenReturn(transactionWithId(5L, 1L));
        mockMvc.perform(get("/transactions/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    void listByAccount_shouldReturnArray() throws Exception {
        Long accountId = 7L;
        when(transactionService.listByAccount(accountId))
                .thenReturn(List.of(transactionWithId(1L, accountId), transactionWithId(2L, accountId)));
        mockMvc.perform(get("/accounts/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountId", is(accountId.intValue())));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        String jsonRequest = """
            {
                "date": "2024-01-15T10:30:00",
                "transactionType": "WITHDRAW",
                "amount": 25.00,
                "balance": 125.00
            }
            """;
        when(transactionService.update(eq(7L), any(TransactionRequest.class)))
                .thenReturn(transactionWithId(7L, 1L));

        mockMvc.perform(put("/transactions/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)));
    }

    private TransactionRequest validRequest() {
        return TransactionRequest.builder()
                .date(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();
    }

    private Transaction transactionWithId(Long id, Long accountId) {
        return Transaction.builder()
                .id(id)
                .accountId(accountId)
                .date(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();
    }
}
