package com.countera.exercise.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Provided end-to-end tests through the REST layer. All of these must pass.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String createCart() throws Exception {
        String body = mockMvc.perform(post("/carts"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartId", notNullValue()))
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);
        return json.get("cartId").asText();
    }

    private void addItem(String cartId, String sku, int qty) throws Exception {
        mockMvc.perform(post("/carts/{id}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"" + sku + "\",\"quantity\":" + qty + "}"))
                .andExpect(status().isOk());
    }

    @Test
    void createAddAndTotal() throws Exception {
        String cartId = createCart();
        addItem(cartId, "BEV-001", 2);
        addItem(cartId, "TOB-001", 1);

        // BEV-001: 598 - 98 promo = 500, tax 36.25 -> 36
        // TOB-001: 899, tax 134.85 -> 135
        mockMvc.perform(get("/carts/{id}/total", cartId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotalCents").value(1497))
                .andExpect(jsonPath("$.discountCents").value(98))
                .andExpect(jsonPath("$.taxCents").value(171))
                .andExpect(jsonPath("$.totalCents").value(1570))
                .andExpect(jsonPath("$.ageVerificationRequired").value(true))
                .andExpect(jsonPath("$.lines.length()").value(2));
    }

    @Test
    void unknownCartIs404() throws Exception {
        mockMvc.perform(get("/carts/{id}/total", "does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void unknownSkuIs404() throws Exception {
        String cartId = createCart();
        mockMvc.perform(post("/carts/{id}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"NOPE-999\",\"quantity\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void zeroQuantityIs400() throws Exception {
        String cartId = createCart();
        mockMvc.perform(post("/carts/{id}/items", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sku\":\"GRO-001\",\"quantity\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }
}
