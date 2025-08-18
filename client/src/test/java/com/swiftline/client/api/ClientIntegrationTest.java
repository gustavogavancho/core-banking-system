package com.swiftline.client.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void fullCrudFlow_shouldPersistThroughAllLayers() throws Exception {
        // 1) Create
        String createJson = """
                {"name":"Jane","gender":"F","age":28,"identification":"ID-999","address":"Addr","phoneNumber":"555","password":"pwd","status":true}
                """;

        MvcResult created = mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/clients/")))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Jane")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        JsonNode body = objectMapper.readTree(created.getResponse().getContentAsString());
        long id = body.get("id").asLong();

        // 2) Get by id
        mockMvc.perform(get("/clients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("Jane")))
                .andExpect(jsonPath("$.password").doesNotExist());

        // 3) List
        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) id)));

        // 4) Update
        String updateJson = """
                {"name":"Jane Updated","gender":"F","age":29,"identification":"ID-999","address":"Addr 2","phoneNumber":"555-1","password":"pwd2","status":false}
                """;
        mockMvc.perform(put("/clients/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.name", is("Jane Updated")))
                .andExpect(jsonPath("$.status", is(false)))
                .andExpect(jsonPath("$.password").doesNotExist());

        // 5) Delete
        mockMvc.perform(delete("/clients/" + id))
                .andExpect(status().isNoContent());

        // 6) Get after delete -> 404
        mockMvc.perform(get("/clients/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Cliente no encontrado")));
    }
}

