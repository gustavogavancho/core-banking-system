package com.swiftline.account.api;

import com.swiftline.account.application.dto.ReportAccountStateResponse;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.application.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReportControllerTest {

    private MockMvc mockMvc;
    private ReportService reportService;

    @BeforeEach
    void setup() {
        reportService = Mockito.mock(ReportService.class);
        ReportController controller = new ReportController(reportService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void report_shouldReturn200_withReportPayload() throws Exception {
        Long clientId = 1L;
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 31, 23, 59, 59);

        ReportAccountStateResponse.TransactionItem tx = ReportAccountStateResponse.TransactionItem.builder()
                .id(5L)
                .date(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
                .transactionType("DEPOSIT")
                .amount(new BigDecimal("50.00"))
                .balance(new BigDecimal("150.00"))
                .build();

        ReportAccountStateResponse.AccountReport account = ReportAccountStateResponse.AccountReport.builder()
                .accountId(10L)
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .balance(new BigDecimal("150.00"))
                .transactions(List.of(tx))
                .build();

        ReportAccountStateResponse response = ReportAccountStateResponse.builder()
                .clientId(clientId)
                .from(from)
                .to(to)
                .accounts(List.of(account))
                .build();

        when(reportService.generate(clientId, from, to)).thenReturn(response);

        mockMvc.perform(get("/report")
                        .param("clientId", String.valueOf(clientId))
                        .param("from", "2024-01-01T00:00:00")
                        .param("to", "2024-01-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId", is(clientId.intValue())))
                .andExpect(jsonPath("$.from", is("2024-01-01T00:00:00")))
                .andExpect(jsonPath("$.to", is("2024-01-31T23:59:59")))
                .andExpect(jsonPath("$.accounts", hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].accountId", is(10)))
                .andExpect(jsonPath("$.accounts[0].accountNumber", is("0001")))
                .andExpect(jsonPath("$.accounts[0].transactions", hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].transactions[0].transactionType", is("DEPOSIT")))
                .andExpect(jsonPath("$.accounts[0].transactions[0].date", is("2024-01-15T10:30:00")));

        verify(reportService, times(1)).generate(clientId, from, to);
    }

    @Test
    void report_shouldReturn404_whenClientNotFound() throws Exception {
        Long clientId = 999L;
        LocalDateTime from = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2024, 1, 31, 23, 59, 59);

        when(reportService.generate(clientId, from, to))
                .thenThrow(new NotFoundException("no client"));

        mockMvc.perform(get("/report")
                        .param("clientId", String.valueOf(clientId))
                        .param("from", "2024-01-01T00:00:00")
                        .param("to", "2024-01-31T23:59:59"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("no client")));

        verify(reportService, times(1)).generate(clientId, from, to);
    }

    @Test
    void report_shouldReturn400_onInvalidDateParam() throws Exception {
        mockMvc.perform(get("/report")
                        .param("clientId", "1")
                        .param("from", "BAD-DATE")
                        .param("to", "2024-01-31T23:59:59"))
                .andExpect(status().isBadRequest());

        verify(reportService, never()).generate(anyLong(), any(), any());
    }
}
