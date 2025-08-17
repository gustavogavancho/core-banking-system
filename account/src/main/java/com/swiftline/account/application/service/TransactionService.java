package com.swiftline.account.application.service;

import com.swiftline.account.application.dto.TransactionRequest;
import com.swiftline.account.domain.model.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction create(Long accountId, TransactionRequest request);
    Transaction get(Long id);
    List<Transaction> listByAccount(Long accountId);
    Transaction update(Long id, TransactionRequest request);
}
