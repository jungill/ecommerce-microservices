package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController — Tests d'intégration")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;

    private ProductResponse productResponse;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productResponse = ProductResponse.builder()
                .id(1L)
                .name("Clavier mécanique")
                .description("RGB switches bleus")
                .price(new BigDecimal("79.99"))
                .stock(15)
                .category("Informatique")
                .build();

        productRequest = new ProductRequest();
        productRequest.setName("Clavier mécanique");
        productRequest.setDescription("RGB switches bleus");
        productRequest.setPrice(new BigDecimal("79.99"));
        productRequest.setStock(15);
        productRequest.setCategory("Informatique");
    }

    // ── POST /api/products ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/products — 201 avec body valide")
    void create_shouldReturn201() throws Exception {
        when(service.create(any(ProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Clavier mécanique"))
                .andExpect(jsonPath("$.price").value(79.99))
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    @DisplayName("POST /api/products — 400 si nom vide")
    void create_shouldReturn400WhenNameEmpty() throws Exception {
        productRequest.setName("");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/products — 400 si prix null")
    void create_shouldReturn400WhenPriceNull() throws Exception {
        productRequest.setPrice(null);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/products — 400 si stock négatif")
    void create_shouldReturn400WhenStockNegative() throws Exception {
        productRequest.setStock(-1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── GET /api/products ─────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products — 200 avec liste de produits")
    void findAll_shouldReturn200() throws Exception {
        when(service.findAll()).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Clavier mécanique"));
    }

    @Test
    @DisplayName("GET /api/products — 200 avec liste vide")
    void findAll_shouldReturn200WhenEmpty() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/products/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/{id} — 200 si produit trouvé")
    void findById_shouldReturn200() throws Exception {
        when(service.findById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Clavier mécanique"));
    }

    @Test
    @DisplayName("GET /api/products/{id} — 404 si produit inexistant")
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(service.findById(999L))
                .thenThrow(new RuntimeException("Produit introuvable : 999"));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Produit introuvable : 999"));
    }

    // ── GET /api/products/search ──────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/products/search — 200 avec résultats")
    void search_shouldReturn200() throws Exception {
        when(service.search("clavier")).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products/search").param("name", "clavier"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Clavier mécanique"));
    }

    // ── PUT /api/products/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /api/products/{id} — 200 avec produit mis à jour")
    void update_shouldReturn200() throws Exception {
        when(service.update(eq(1L), any(ProductRequest.class))).thenReturn(productResponse);

        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/products/{id} — 404 si produit inexistant")
    void update_shouldReturn404WhenNotFound() throws Exception {
        when(service.update(eq(999L), any(ProductRequest.class)))
                .thenThrow(new RuntimeException("Produit introuvable : 999"));

        mockMvc.perform(put("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/products/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/products/{id} — 204 si produit supprimé")
    void delete_shouldReturn204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} — 404 si produit inexistant")
    void delete_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new RuntimeException("Produit introuvable : 999"))
                .when(service).delete(999L);

        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());
    }
}
