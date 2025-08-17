package com.swiftline.account.api;

import com.swiftline.account.application.dto.TransactionRequest;
import com.swiftline.account.application.dto.TransactionResponse;
import com.swiftline.account.application.service.TransactionService;
import com.swiftline.account.domain.model.Transaction;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class TransactionController {

    private final TransactionService transactionService;
    private final ModelMapper mapper;

    public TransactionController(TransactionService transactionService, ModelMapper mapper) {
        this.transactionService = transactionService;
        this.mapper = mapper;
    }

    @PostMapping("/accounts/{accountId}/transactions")
    public ResponseEntity<TransactionResponse> create(@PathVariable Long accountId,
                                                      @Valid @RequestBody TransactionRequest request) {
        Transaction created = transactionService.create(accountId, request);
        TransactionResponse body = toResponse(created);
        return ResponseEntity.created(URI.create("/transactions/" + body.getId())).body(body);
    }

    @GetMapping("/transactions/{id}")
    public TransactionResponse get(@PathVariable Long id) {
        return toResponse(transactionService.get(id));
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public List<TransactionResponse> listByAccount(@PathVariable Long accountId) {
        return transactionService.listByAccount(accountId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @PutMapping("/transactions/{id}")
    public TransactionResponse update(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return toResponse(transactionService.update(id, request));
    }

    @DeleteMapping("/transactions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        transactionService.delete(id);
    }

    private TransactionResponse toResponse(Transaction t) {
        return mapper.map(t, TransactionResponse.class);
    }
}
