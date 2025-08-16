package com.swiftline.client.application.service;

import com.swiftline.client.application.dto.ClientRequest;
import com.swiftline.client.application.exception.NotFoundException;
import com.swiftline.client.domain.model.Client;
import com.swiftline.client.domain.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    private ClientRepository clientRepository;
    private ClientServiceImpl service;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        service = new ClientServiceImpl(clientRepository);
    }

    @Test
    void create_shouldPersistAndReturnClient() {
        ClientRequest req = validRequest();
        Client saved = clientWithId(10L);
        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        Client result = service.create(req);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client toSave = captor.getValue();
        assertEquals(req.getName(), toSave.getName());
        assertEquals(req.getPassword(), toSave.getPassword());
    }

    @Test
    void get_shouldReturnClient_whenExists() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(clientWithId(1L)));
        Client c = service.get(1L);
        assertEquals(1L, c.getId());
    }

    @Test
    void get_shouldThrowNotFound_whenMissing() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.get(99L));
    }

    @Test
    void list_shouldReturnAll() {
        when(clientRepository.findAll()).thenReturn(List.of(clientWithId(1L), clientWithId(2L)));
        List<Client> list = service.list();
        assertEquals(2, list.size());
    }

    @Test
    void update_shouldUpdate_whenExists() {
        ClientRequest req = validRequest();
        when(clientRepository.existsById(5L)).thenReturn(true);
        when(clientRepository.update(eq(5L), any(Client.class))).thenReturn(clientWithId(5L));

        Client updated = service.update(5L, req);
        assertEquals(5L, updated.getId());
    }

    @Test
    void update_shouldThrowNotFound_whenMissing() {
        when(clientRepository.existsById(5L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.update(5L, validRequest()));
        verify(clientRepository, never()).update(anyLong(), any());
    }

    @Test
    void delete_shouldDelete_whenExists() {
        when(clientRepository.existsById(7L)).thenReturn(true);
        service.delete(7L);
        verify(clientRepository).deleteById(7L);
    }

    @Test
    void delete_shouldThrowNotFound_whenMissing() {
        when(clientRepository.existsById(7L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete(7L));
        verify(clientRepository, never()).deleteById(anyLong());
    }

    private ClientRequest validRequest() {
        return ClientRequest.builder()
                .name("John Doe")
                .gender("M")
                .age(30)
                .identification("ID-123")
                .address("Street 123")
                .phoneNumber("555-111")
                .password("secret")
                .state(true)
                .build();
    }

    private Client clientWithId(Long id) {
        Client c = new Client();
        c.setId(id);
        c.setName("John Doe");
        c.setGender("M");
        c.setAge(30);
        c.setIdentification("ID-123");
        c.setAddress("Street 123");
        c.setPhoneNumber("555-111");
        c.setPassword("secret");
        c.setState(true);
        return c;
    }
}

