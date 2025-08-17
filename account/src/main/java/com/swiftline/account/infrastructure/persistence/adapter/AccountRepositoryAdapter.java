package com.swiftline.account.infrastructure.persistence.adapter;

import com.swiftline.account.domain.model.Account;
import com.swiftline.account.domain.repository.AccountRepository;
import com.swiftline.account.infrastructure.persistence.entity.AccountEntity;
import com.swiftline.account.infrastructure.persistence.repository.AccountJpaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AccountRepositoryAdapter implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;
    private final ModelMapper mapper;

    public AccountRepositoryAdapter(AccountJpaRepository accountJpaRepository, ModelMapper mapper) {
        this.accountJpaRepository = accountJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = toEntity(account);
        AccountEntity saved = accountJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> findById(Long id) {
        return accountJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> findAll() {
        return accountJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Account update(Long id, Account account) {
        AccountEntity existing = accountJpaRepository.findById(id).orElseThrow();
        existing.setAccountNumber(account.getAccountNumber());
        existing.setAccountType(account.getAccountType());
        existing.setInitialBalance(account.getInitialBalance());
        existing.setStatus(account.getStatus());
        AccountEntity saved = accountJpaRepository.save(existing);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (accountJpaRepository.existsById(id)) {
            accountJpaRepository.deleteById(id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return accountJpaRepository.existsById(id);
    }

    private Account toDomain(AccountEntity entity) {
        return mapper.map(entity, Account.class);
    }

    private AccountEntity toEntity(Account account) {
        return mapper.map(account, AccountEntity.class);
    }
}

