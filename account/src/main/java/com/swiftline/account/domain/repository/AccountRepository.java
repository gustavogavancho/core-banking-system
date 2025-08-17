package com.swiftline.account.domain.repository;

import com.swiftline.account.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Account save(Account account);
    Optional<Account> findById(Long id);
    List<Account> findAll();
    Account update(Long id, Account account);
    boolean existsById(Long id);
}
