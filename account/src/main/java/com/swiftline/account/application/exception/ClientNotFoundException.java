package com.swiftline.account.application.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }

    public ClientNotFoundException(Long clientId) {
        super("Cliente no encontrado con id=" + clientId);
    }
}
