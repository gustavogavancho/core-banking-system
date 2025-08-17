package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.AccountRequest;
import com.swiftline.account.domain.model.Account;

import java.util.List;

public interface AccountService {
    Account create(AccountRequest request);
    Account get(Long id);
    List<Account> list();
    Account update(Long id, AccountRequest request);
}
