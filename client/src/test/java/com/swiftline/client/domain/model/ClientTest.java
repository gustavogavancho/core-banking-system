package com.swiftline.client.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void superBuilder_shouldSetAllFields() {
        Client client = Client.builder()
                .id(1L)
                .name("John")
                .gender("M")
                .age(30)
                .identification("ID123")
                .address("Street 1")
                .phoneNumber("555-000")
                .password("secret")
                .status(true)
                .build();

        assertAll(
                () -> assertEquals(1L, client.getId()),
                () -> assertEquals("John", client.getName()),
                () -> assertEquals("M", client.getGender()),
                () -> assertEquals(30, client.getAge()),
                () -> assertEquals("ID123", client.getIdentification()),
                () -> assertEquals("Street 1", client.getAddress()),
                () -> assertEquals("555-000", client.getPhoneNumber()),
                () -> assertEquals("secret", client.getPassword()),
                () -> assertTrue(client.getStatus())
        );
    }

    @Test
    void settersAndGetters_shouldWork() {
        Client client = new Client();
        client.setId(2L);
        client.setName("Jane");
        client.setGender("F");
        client.setAge(28);
        client.setIdentification("ID456");
        client.setAddress("Street 2");
        client.setPhoneNumber("555-111");
        client.setPassword("pwd");
        client.setStatus(false);

        assertAll(
                () -> assertEquals(2L, client.getId()),
                () -> assertEquals("Jane", client.getName()),
                () -> assertEquals("F", client.getGender()),
                () -> assertEquals(28, client.getAge()),
                () -> assertEquals("ID456", client.getIdentification()),
                () -> assertEquals("Street 2", client.getAddress()),
                () -> assertEquals("555-111", client.getPhoneNumber()),
                () -> assertEquals("pwd", client.getPassword()),
                () -> assertFalse(client.getStatus())
        );
    }

    @Test
    void allowsNullables() {
        Client client = Client.builder()
                .id(null)
                .name(null)
                .gender(null)
                .age(null)
                .identification(null)
                .address(null)
                .phoneNumber(null)
                .password(null)
                .status(null)
                .build();

        assertAll(
                () -> assertNull(client.getId()),
                () -> assertNull(client.getName()),
                () -> assertNull(client.getGender()),
                () -> assertNull(client.getAge()),
                () -> assertNull(client.getIdentification()),
                () -> assertNull(client.getAddress()),
                () -> assertNull(client.getPhoneNumber()),
                () -> assertNull(client.getPassword()),
                () -> assertNull(client.getStatus())
        );
    }

    @Test
    void allArgsConstructor_shouldInitOwnFieldsOnly() {
        Client client = new Client("p", true);
        assertAll(
                () -> assertEquals("p", client.getPassword()),
                () -> assertTrue(client.getStatus()),
                () -> assertNull(client.getId()),
                () -> assertNull(client.getName()),
                () -> assertNull(client.getGender()),
                () -> assertNull(client.getAge()),
                () -> assertNull(client.getIdentification()),
                () -> assertNull(client.getAddress()),
                () -> assertNull(client.getPhoneNumber())
        );
    }
}

