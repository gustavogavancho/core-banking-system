package com.swiftline.account.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class ClientValidationServiceImpl implements ClientValidationService {

    private final RestTemplate restTemplate;
    private final String clientServiceUrl;

    public ClientValidationServiceImpl(RestTemplate restTemplate,
                                       @Value("${client.service.url:http://localhost:8081}") String clientServiceUrl) {
        this.restTemplate = restTemplate;
        this.clientServiceUrl = clientServiceUrl;
    }

    @Override
    public boolean existsById(Long clientId) {
        try {
            String url = clientServiceUrl + "/clients/" + clientId;
            restTemplate.getForEntity(url, Void.class);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw new RuntimeException("Error verificando cliente: " + e.getMessage(), e);
        }
    }
}
