package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.service.OrderService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController — Tests d'intégration")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService service;

    private OrderResponse orderResponse;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        orderResponse = OrderResponse.builder()
                .id(1L)
                .productId(1L)
                .productName("Clavier mécanique")
                .quantity(2)
                .totalPrice(new BigDecimal("159.98"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        orderRequest = new OrderRequest();
        orderRequest.setProductId(1L);
        orderRequest.setQuantity(2);
    }

    // ── POST /api/orders ──────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/orders — 201 avec body valide")
    void create_shouldReturn201() throws Exception {
        when(service.create(any(OrderRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Clavier mécanique"))
                .andExpect(jsonPath("$.totalPrice").value(159.98))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/orders — 400 si productId null")
    void create_shouldReturn400WhenProductIdNull() throws Exception {
        orderRequest.setProductId(null);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/orders — 400 si quantité inférieure à 1")
    void create_shouldReturn400WhenQuantityZero() throws Exception {
        orderRequest.setQuantity(0);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── GET /api/orders ───────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/orders — 200 avec liste de commandes")
    void findAll_shouldReturn200() throws Exception {
        when(service.findAll()).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/orders — 200 avec liste vide")
    void findAll_shouldReturn200WhenEmpty() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ── GET /api/orders/{id} ──────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/orders/{id} — 200 si commande trouvée")
    void findById_shouldReturn200() throws Exception {
        when(service.findById(1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} — 404 si commande inexistante")
    void findById_shouldReturn404WhenNotFound() throws Exception {
        when(service.findById(999L))
                .thenThrow(new RuntimeException("Commande introuvable : 999"));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Commande introuvable : 999"));
    }

    // ── PATCH /api/orders/{id}/confirm ────────────────────────────────────────

    @Test
    @DisplayName("PATCH /api/orders/{id}/confirm — 200 si commande confirmée")
    void confirm_shouldReturn200() throws Exception {
        OrderResponse confirmed = OrderResponse.builder()
                .id(1L).productId(1L).productName("Clavier mécanique")
                .quantity(2).totalPrice(new BigDecimal("159.98"))
                .status(OrderStatus.CONFIRMED).createdAt(LocalDateTime.now()).build();
        when(service.confirm(1L)).thenReturn(confirmed);

        mockMvc.perform(patch("/api/orders/1/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/confirm — 404 si commande inexistante")
    void confirm_shouldReturn404WhenNotFound() throws Exception {
        when(service.confirm(999L))
                .thenThrow(new RuntimeException("Commande introuvable : 999"));

        mockMvc.perform(patch("/api/orders/999/confirm"))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /api/orders/{id}/cancel ─────────────────────────────────────────

    @Test
    @DisplayName("PATCH /api/orders/{id}/cancel — 200 si commande annulée")
    void cancel_shouldReturn200() throws Exception {
        OrderResponse cancelled = OrderResponse.builder()
                .id(1L).productId(1L).productName("Clavier mécanique")
                .quantity(2).totalPrice(new BigDecimal("159.98"))
                .status(OrderStatus.CANCELLED).createdAt(LocalDateTime.now()).build();
        when(service.cancel(1L)).thenReturn(cancelled);

        mockMvc.perform(patch("/api/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/cancel — 404 si commande inexistante")
    void cancel_shouldReturn404WhenNotFound() throws Exception {
        when(service.cancel(999L))
                .thenThrow(new RuntimeException("Commande introuvable : 999"));

        mockMvc.perform(patch("/api/orders/999/cancel"))
                .andExpect(status().isNotFound());
    }
}
