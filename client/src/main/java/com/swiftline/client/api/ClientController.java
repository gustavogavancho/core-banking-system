package com.swiftline.client.api;

import com.swiftline.client.application.dto.ClientRequest;
import com.swiftline.client.application.dto.ClientResponse;
import com.swiftline.client.application.service.ClientService;
import com.swiftline.client.domain.model.Client;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody ClientRequest request) {
        Client created = clientService.create(request);
        ClientResponse body = toResponse(created);
        return ResponseEntity.created(URI.create("/clients/" + body.getId())).body(body);
    }

    @GetMapping("/{id}")
    public ClientResponse get(@PathVariable Long id) {
        return toResponse(clientService.get(id));
    }

    @GetMapping
    public List<ClientResponse> list() {
        return clientService.list().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ClientResponse update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return toResponse(clientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }

    private ClientResponse toResponse(Client c) {
        return ClientResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .gender(c.getGender())
                .age(c.getAge())
                .identification(c.getIdentification())
                .address(c.getAddress())
                .phoneNumber(c.getPhoneNumber())
                .state(c.getState())
                .build();
    }
}

