package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.AccountRequest;
import com.swiftline.account.application.exception.NotFoundException;
import com.swiftline.account.domain.model.Account;
import com.swiftline.account.domain.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final ModelMapper mapper;

    public AccountServiceImpl(AccountRepository accountRepository, ModelMapper mapper) {
        this.accountRepository = accountRepository;
        this.mapper = mapper;
    }

    @Override
    public Account create(AccountRequest request) {
        Account account = toDomain(request);
        return accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account get(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cuenta no encontrada con id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> list() {
        return accountRepository.findAll();
    }

    @Override
    public Account update(Long id, AccountRequest request) {
        if (!accountRepository.existsById(id)) {
            throw new NotFoundException("Cuenta no encontrada con id=" + id);
        }
        Account account = toDomain(request);
        return accountRepository.update(id, account);
    }

    @Override
    public void delete(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new NotFoundException("Cuenta no encontrada con id=" + id);
        }
        accountRepository.deleteById(id);
    }

    private Account toDomain(AccountRequest r) {
        return mapper.map(r, Account.class);
    }
}

