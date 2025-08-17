package com.swiftline.account.infrastructure.persistence.adapter;

import com.swiftline.account.config.ModelMapperConfig;
import com.swiftline.account.domain.model.Account;
import com.swiftline.account.infrastructure.persistence.entity.AccountEntity;
import com.swiftline.account.infrastructure.persistence.repository.AccountJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AccountRepositoryAdapterTest {

    private AccountJpaRepository jpa;
    private AccountRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        jpa = mock(AccountJpaRepository.class);
        ModelMapper mapper = new ModelMapperConfig().modelMapper();
        adapter = new AccountRepositoryAdapter(jpa, mapper);
    }

    @Test
    void save_shouldPersist_andReturnDomain() {
        Account toSave = Account.builder()
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .build();
        when(jpa.save(any(AccountEntity.class))).thenAnswer(inv -> {
            AccountEntity e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });
        Account out = adapter.save(toSave);
        assertNotNull(out.getId());
        assertEquals("0001", out.getAccountNumber());
    }

    @Test
    void findById_shouldMapToDomain_whenPresent() {
        AccountEntity e = entity(5L);
        when(jpa.findById(5L)).thenReturn(Optional.of(e));
        Optional<Account> out = adapter.findById(5L);
        assertTrue(out.isPresent());
        assertEquals(5L, out.get().getId());
    }

    @Test
    void findAll_shouldMapList() {
        when(jpa.findAll()).thenReturn(List.of(entity(1L), entity(2L)));
        List<Account> out = adapter.findAll();
        assertEquals(2, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals(2L, out.get(1).getId());
    }

    @Test
    void update_shouldChangeFields_andReturnDomain() {
        AccountEntity existing = entity(7L);
        when(jpa.findById(7L)).thenReturn(Optional.of(existing));
        when(jpa.save(any(AccountEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Account newData = Account.builder()
                .accountNumber("0002")
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("200.00"))
                .status(false)
                .build();
        Account out = adapter.update(7L, newData);
        assertEquals(7L, out.getId());
        assertEquals("0002", out.getAccountNumber());
        assertEquals("CHECKING", out.getAccountType());
        assertEquals(new BigDecimal("200.00"), out.getInitialBalance());
        assertFalse(out.getStatus());
    }

    @Test
    void deleteById_shouldCallJpa_whenExists() {
        when(jpa.existsById(3L)).thenReturn(true);
        doNothing().when(jpa).deleteById(3L);
        adapter.deleteById(3L);
        verify(jpa).deleteById(3L);
    }

    @Test
    void existsById_shouldDelegate() {
        when(jpa.existsById(1L)).thenReturn(true);
        assertTrue(adapter.existsById(1L));
    }

    private AccountEntity entity(Long id) {
        return AccountEntity.builder()
                .id(id)
                .accountNumber("0001")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("100.00"))
                .status(true)
                .build();
    }
}

