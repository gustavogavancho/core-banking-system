package com.swiftline.account.api;

import com.swiftline.account.application.dto.AccountRequest;
import com.swiftline.account.application.dto.AccountResponse;
import com.swiftline.account.application.service.AccountService;
import com.swiftline.account.domain.model.Account;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final ModelMapper mapper;

    public AccountController(AccountService accountService, ModelMapper mapper) {
        this.accountService = accountService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest request) {
        Account created = accountService.create(request);
        AccountResponse body = toResponse(created);
        return ResponseEntity.created(URI.create("/accounts/" + body.getId())).body(body);
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable Long id) {
        return toResponse(accountService.get(id));
    }

    @GetMapping
    public List<AccountResponse> list() {
        return accountService.list().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Long id, @Valid @RequestBody AccountRequest request) {
        return toResponse(accountService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        accountService.delete(id);
    }

    private AccountResponse toResponse(Account a) {
        return mapper.map(a, AccountResponse.class);
    }
}

