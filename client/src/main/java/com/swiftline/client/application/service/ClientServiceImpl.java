package com.swiftline.client.application.service;

import com.swiftline.client.application.dto.ClientRequest;
import com.swiftline.client.application.exception.NotFoundException;
import com.swiftline.client.domain.model.Client;
import com.swiftline.client.domain.repository.ClientRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper mapper;

    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper mapper) {
        this.clientRepository = clientRepository;
        this.mapper = mapper;
    }

    @Override
    public Client create(ClientRequest request) {
        Client client = toDomain(request);
        return clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Client get(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con id=" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> list() {
        return clientRepository.findAll();
    }

    @Override
    public Client update(Long id, ClientRequest request) {
        if (!clientRepository.existsById(id)) {
            throw new NotFoundException("Cliente no encontrado con id=" + id);
        }
        Client client = toDomain(request);
        return clientRepository.update(id, client);
    }

    @Override
    public void delete(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new NotFoundException("Cliente no encontrado con id=" + id);
        }
        clientRepository.deleteById(id);
    }

    private Client toDomain(ClientRequest r) {
        return mapper.map(r, Client.class);
    }
}
