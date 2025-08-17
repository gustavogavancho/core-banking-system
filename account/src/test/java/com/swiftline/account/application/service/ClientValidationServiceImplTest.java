package com.swiftline.account.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ClientValidationServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private ClientValidationService service;
    private final String clientServiceUrl = "http://localhost:8081";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ClientValidationServiceImpl(restTemplate, clientServiceUrl);
    }

    @Test
    void existsById_shouldReturnTrue_whenClientExists() {
        // Given
        Long clientId = 123L;
        when(restTemplate.getForEntity(eq("http://localhost:8081/clients/123"), eq(Void.class)))
                .thenReturn(null); // No importa el retorno, solo que no lance excepción

        // When
        boolean exists = service.existsById(clientId);

        // Then
        assertTrue(exists);
        verify(restTemplate).getForEntity("http://localhost:8081/clients/123", Void.class);
    }

    @Test
    void existsById_shouldReturnFalse_whenClientNotFound() {
        // Given
        Long clientId = 999L;
        when(restTemplate.getForEntity(eq("http://localhost:8081/clients/999"), eq(Void.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When
        boolean exists = service.existsById(clientId);

        // Then
        assertFalse(exists);
    }

    @Test
    void existsById_shouldThrowException_whenServiceError() {
        // Given
        Long clientId = 123L;
        when(restTemplate.getForEntity(any(String.class), eq(Void.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            service.existsById(clientId));

        assertTrue(exception.getMessage().contains("Error verificando cliente"));
    }
}
