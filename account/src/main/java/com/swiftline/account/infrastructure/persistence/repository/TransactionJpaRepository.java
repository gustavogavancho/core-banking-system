package com.swiftline.account.infrastructure.persistence.repository;

import com.swiftline.account.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByAccount_Id(Long accountId);
}

