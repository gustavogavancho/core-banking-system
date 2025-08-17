package com.swiftline.account.api;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        Long accountId = 10L;
        TransactionRequest req = validRequest();
        Transaction created = transactionWithId(1L, accountId);
        when(transactionService.create(eq(accountId), any(TransactionRequest.class))).thenReturn(created);

        mockMvc.perform(post("/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/transactions/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountId", is(accountId.intValue())))
                .andExpect(jsonPath("$.transactionType", is(req.getTransactionType())));
    }

    @Test
    void create_shouldReturn404_whenAccountMissing() throws Exception {
        Long accountId = 99L;
        TransactionRequest req = validRequest();
        when(transactionService.create(eq(accountId), any(TransactionRequest.class)))
                .thenThrow(new NotFoundException("no account"));

        mockMvc.perform(post("/accounts/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
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
        TransactionRequest req = validRequest();
        when(transactionService.update(eq(7L), any(TransactionRequest.class)))
                .thenReturn(transactionWithId(7L, 1L));

        mockMvc.perform(put("/transactions/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)));
    }

    private TransactionRequest validRequest() {
        return TransactionRequest.builder()
                .date(LocalDateTime.now())
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();
    }

    private Transaction transactionWithId(Long id, Long accountId) {
        return Transaction.builder()
                .id(id)
                .accountId(accountId)
                .date(LocalDateTime.now())
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();
    }
}
