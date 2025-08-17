package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.AccountRequest;
import com.swiftline.account.application.exception.ClientNotFoundException;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.domain.model.Account;
import com.swiftline.account.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    private AccountRepository repo;
    private ClientValidationService clientValidationService;
    private AccountService service;

    @BeforeEach
    void setup() {
        repo = mock(AccountRepository.class);
        clientValidationService = mock(ClientValidationService.class);
        service = new AccountServiceImpl(repo, clientValidationService, new ModelMapper());
    }

    @Test
    void create_shouldMapAndSave_whenClientExists() {
        AccountRequest req = AccountRequest.builder()
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .clientId(123L)
                .build();

        when(clientValidationService.existsById(123L)).thenReturn(true);
        when(repo.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        Account out = service.create(req);

        assertNotNull(out.getId());
        assertEquals(req.getAccountNumber(), out.getAccountNumber());
        assertEquals(123L, out.getClientId());
        verify(clientValidationService).existsById(123L);
    }

    @Test
    void create_shouldThrowClientNotFoundException_whenClientDoesNotExist() {
        AccountRequest req = AccountRequest.builder()
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .clientId(999L)
                .build();

        when(clientValidationService.existsById(999L)).thenReturn(false);

        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () ->
                service.create(req));

        assertEquals("Cliente no encontrado con id=999", exception.getMessage());
        verify(clientValidationService).existsById(999L);
        verify(repo, never()).save(any(Account.class));
    }

    @Test
    void get_shouldReturn_whenFound() {
        when(repo.findById(5L)).thenReturn(Optional.of(Account.builder().id(5L).clientId(10L).build()));
        Account out = service.get(5L);
        assertEquals(5L, out.getId());
        assertEquals(10L, out.getClientId());
    }

    @Test
    void get_shouldThrow_whenMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(99L));
    }

    @Test
    void list_shouldReturnAll() {
        when(repo.findAll()).thenReturn(List.of(Account.builder().id(1L).clientId(10L).build()));
        List<Account> out = service.list();
        assertEquals(1, out.size());
        assertEquals(10L, out.get(0).getClientId());
    }

    @Test
    void update_shouldUpdateAccount_whenClientExists() {
        when(repo.existsById(7L)).thenReturn(true);
        when(clientValidationService.existsById(123L)).thenReturn(true);
        when(repo.update(eq(7L), any(Account.class))).thenReturn(Account.builder().id(7L).clientId(123L).build());

        AccountRequest req = AccountRequest.builder()
                .accountNumber("0002").accountType("CHECKING")
                .initialBalance(new BigDecimal("200.00")).status(true).clientId(123L).build();

        Account out = service.update(7L, req);

        assertEquals(7L, out.getId());
        assertEquals(123L, out.getClientId());
        verify(clientValidationService).existsById(123L);
    }

    @Test
    void update_shouldThrowClientNotFoundException_whenClientDoesNotExist() {
        when(repo.existsById(7L)).thenReturn(true);
        when(clientValidationService.existsById(999L)).thenReturn(false);

        AccountRequest req = AccountRequest.builder()
                .accountNumber("0002").accountType("CHECKING")
                .initialBalance(new BigDecimal("200.00")).status(true).clientId(999L).build();

        ClientNotFoundException exception = assertThrows(ClientNotFoundException.class, () ->
                service.update(7L, req));

        assertEquals("Cliente no encontrado con id=999", exception.getMessage());
        verify(clientValidationService).existsById(999L);
        verify(repo, never()).update(any(Long.class), any(Account.class));
    }
}
