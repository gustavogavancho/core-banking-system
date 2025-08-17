package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.AccountRequest;
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
    private AccountService service;

    @BeforeEach
    void setup() {
        repo = mock(AccountRepository.class);
        service = new AccountServiceImpl(repo, new ModelMapper());
    }

    @Test
    void create_shouldMapAndSave() {
        AccountRequest req = AccountRequest.builder()
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .build();
        when(repo.save(any(Account.class))).thenAnswer(inv -> {
            Account a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });
        Account out = service.create(req);
        assertNotNull(out.getId());
        assertEquals(req.getAccountNumber(), out.getAccountNumber());
    }

    @Test
    void get_shouldReturn_whenFound() {
        when(repo.findById(5L)).thenReturn(Optional.of(Account.builder().id(5L).build()));
        Account out = service.get(5L);
        assertEquals(5L, out.getId());
    }

    @Test
    void get_shouldThrow_whenMissing() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(99L));
    }

    @Test
    void list_shouldReturnAll() {
        when(repo.findAll()).thenReturn(List.of(Account.builder().id(1L).build()));
        List<Account> out = service.list();
        assertEquals(1, out.size());
    }

    @Test
    void update_shouldCallRepo_whenExists() {
        when(repo.existsById(7L)).thenReturn(true);
        when(repo.update(eq(7L), any(Account.class))).thenReturn(Account.builder().id(7L).build());
        AccountRequest req = AccountRequest.builder()
                .accountNumber("0002").accountType("CHECKING")
                .initialBalance(new BigDecimal("200.00")).status(true).build();
        Account out = service.update(7L, req);
        assertEquals(7L, out.getId());
    }

    @Test
    void update_shouldThrow_whenMissing() {
        when(repo.existsById(7L)).thenReturn(false);
        AccountRequest req = AccountRequest.builder()
                .accountNumber("0002").accountType("CHECKING")
                .initialBalance(new BigDecimal("200.00")).status(true).build();
        assertThrows(NotFoundException.class, () -> service.update(7L, req));
    }

    @Test
    void delete_shouldCallRepo_whenExists() {
        when(repo.existsById(3L)).thenReturn(true);
        doNothing().when(repo).deleteById(3L);
        assertDoesNotThrow(() -> service.delete(3L));
        verify(repo).deleteById(3L);
    }

    @Test
    void delete_shouldThrow_whenMissing() {
        when(repo.existsById(3L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete(3L));
    }
}

