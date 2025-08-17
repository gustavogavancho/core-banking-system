package com.swiftline.account.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftline.account.application.dto.AccountRequest;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.application.service.AccountService;
import com.swiftline.account.domain.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {

    private MockMvc mockMvc;
    private AccountService accountService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        accountService = Mockito.mock(AccountService.class);
        AccountController controller = new AccountController(accountService, new ModelMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201() throws Exception {
        AccountRequest req = validRequest();
        Account created = accountWithId(1L);
        when(accountService.create(any(AccountRequest.class))).thenReturn(created);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/accounts/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.accountNumber", is(req.getAccountNumber())));
    }

    @Test
    void create_shouldReturn400_onValidationErrors() throws Exception {
        AccountRequest bad = validRequest();
        bad.setAccountNumber(""); // NotBlank

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.accountNumber", notNullValue()));
    }

    @Test
    void get_shouldReturn200_whenFound() throws Exception {
        when(accountService.get(5L)).thenReturn(accountWithId(5L));
        mockMvc.perform(get("/accounts/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    void get_shouldReturn404_whenNotFound() throws Exception {
        when(accountService.get(99L)).thenThrow(new NotFoundException("not found"));
        mockMvc.perform(get("/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("not found")));
    }

    @Test
    void list_shouldReturnArray() throws Exception {
        when(accountService.list()).thenReturn(List.of(accountWithId(1L), accountWithId(2L)));
        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        AccountRequest req = validRequest();
        when(accountService.update(eq(7L), any(AccountRequest.class))).thenReturn(accountWithId(7L));

        mockMvc.perform(put("/accounts/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(accountService).delete(3L);
        mockMvc.perform(delete("/accounts/3"))
                .andExpect(status().isNoContent());
    }

    private AccountRequest validRequest() {
        return AccountRequest.builder()
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .build();
    }

    private Account accountWithId(Long id) {
        return Account.builder()
                .id(id)
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .build();
    }
}

