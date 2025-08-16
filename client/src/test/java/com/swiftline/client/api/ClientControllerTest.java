package com.swiftline.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftline.client.application.dto.ClientRequest;
import com.swiftline.client.application.exception.NotFoundException;
import com.swiftline.client.application.service.ClientService;
import com.swiftline.client.domain.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClientControllerTest {

    private MockMvc mockMvc;
    private ClientService clientService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        clientService = Mockito.mock(ClientService.class);
        ClientController controller = new ClientController(clientService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void create_shouldReturn201_andBody_withoutPassword() throws Exception {
        ClientRequest req = validRequest();
        Client created = clientWithId(1L);
        when(clientService.create(any(ClientRequest.class))).thenReturn(created);

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/clients/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(req.getName())))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void create_shouldReturn400_onValidationErrors() throws Exception {
        ClientRequest bad = validRequest();
        bad.setName(""); // NotBlank

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name", notNullValue()));
    }

    @Test
    void get_shouldReturn200_whenFound() throws Exception {
        when(clientService.get(5L)).thenReturn(clientWithId(5L));
        mockMvc.perform(get("/clients/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    void get_shouldReturn404_whenNotFound() throws Exception {
        when(clientService.get(99L)).thenThrow(new NotFoundException("not found"));
        mockMvc.perform(get("/clients/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("not found")));
    }

    @Test
    void list_shouldReturnArray() throws Exception {
        when(clientService.list()).thenReturn(List.of(clientWithId(1L), clientWithId(2L)));
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        ClientRequest req = validRequest();
        when(clientService.update(eq(7L), any(ClientRequest.class))).thenReturn(clientWithId(7L));

        mockMvc.perform(put("/clients/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(clientService).delete(3L);
        mockMvc.perform(delete("/clients/3"))
                .andExpect(status().isNoContent());
    }

    private ClientRequest validRequest() {
        return ClientRequest.builder()
                .name("Jane")
                .gender("F")
                .age(28)
                .identification("ABC")
                .address("Addr")
                .phoneNumber("555")
                .password("pwd")
                .state(true)
                .build();
    }

    private Client clientWithId(Long id) {
        Client c = new Client();
        c.setId(id);
        c.setName("Jane");
        c.setGender("F");
        c.setAge(28);
        c.setIdentification("ABC");
        c.setAddress("Addr");
        c.setPhoneNumber("555");
        c.setPassword("pwd");
        c.setState(true);
        return c;
    }
}

