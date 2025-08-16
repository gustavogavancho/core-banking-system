package com.swiftline.client.application.service;

import com.swiftline.client.application.dto.ClientRequest;
import com.swiftline.client.domain.model.Client;

import java.util.List;

public interface ClientService {
    Client create(ClientRequest request);
    Client get(Long id);
    List<Client> list();
    Client update(Long id, ClientRequest request);
    void delete(Long id);
}

