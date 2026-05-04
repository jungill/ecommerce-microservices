package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService — Tests unitaires")
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService service;

    private Order order;
    private OrderRequest request;
    private ProductResponse productResponse;

    @BeforeEach
    void setUp() {
        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Clavier mécanique");
        productResponse.setPrice(new BigDecimal("79.99"));
        productResponse.setStock(15);

        request = new OrderRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        order = Order.builder()
                .id(1L)
                .productId(1L)
                .productName("Clavier mécanique")
                .quantity(2)
                .totalPrice(new BigDecimal("159.98"))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() — doit calculer le prix total correct (prix × quantité)")
    void create_shouldCalculateTotalPrice() {
        when(productClient.findById(1L)).thenReturn(productResponse);
        when(repository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = service.create(request);

        // 79.99 × 2 = 159.98
        assertThat(response.getTotalPrice()).isEqualByComparingTo("159.98");
        verify(productClient, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("create() — doit appeler productClient pour récupérer le prix")
    void create_shouldCallProductClient() {
        when(productClient.findById(1L)).thenReturn(productResponse);
        when(repository.save(any(Order.class))).thenReturn(order);

        service.create(request);

        verify(productClient, times(1)).findById(1L);
    }

    @Test
    @DisplayName("create() — le statut initial doit être PENDING")
    void create_shouldSetStatusToPending() {
        when(productClient.findById(1L)).thenReturn(productResponse);
        when(repository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = service.create(request);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("create() — doit stocker le nom du produit")
    void create_shouldStoreProductName() {
        when(productClient.findById(1L)).thenReturn(productResponse);
        when(repository.save(any(Order.class))).thenReturn(order);

        OrderResponse response = service.create(request);

        assertThat(response.getProductName()).isEqualTo("Clavier mécanique");
    }

    // ── FIND ALL ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll() — doit retourner toutes les commandes")
    void findAll_shouldReturnAllOrders() {
        when(repository.findAll()).thenReturn(List.of(order));

        List<OrderResponse> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("findAll() — doit retourner une liste vide si aucune commande")
    void findAll_shouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<OrderResponse> result = service.findAll();

        assertThat(result).isEmpty();
    }

    // ── FIND BY ID ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById() — doit retourner la commande correspondante")
    void findById_shouldReturnOrder() {
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = service.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("findById() — doit lever une exception si la commande n'existe pas")
    void findById_shouldThrowWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }

    // ── CONFIRM ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("confirm() — doit passer le statut à CONFIRMED")
    void confirm_shouldSetStatusToConfirmed() {
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        order.setStatus(OrderStatus.PENDING);
        Order confirmed = Order.builder()
                .id(1L).productId(1L).productName("Clavier mécanique")
                .quantity(2).totalPrice(new BigDecimal("159.98"))
                .status(OrderStatus.CONFIRMED).createdAt(LocalDateTime.now()).build();
        when(repository.save(any(Order.class))).thenReturn(confirmed);

        OrderResponse response = service.confirm(1L);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("confirm() — doit lever une exception si la commande est annulée")
    void confirm_shouldThrowWhenCancelled() {
        order.setStatus(OrderStatus.CANCELLED);
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.confirm(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("annulée");
    }

    // ── CANCEL ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancel() — doit passer le statut à CANCELLED")
    void cancel_shouldSetStatusToCancelled() {
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        Order cancelled = Order.builder()
                .id(1L).productId(1L).productName("Clavier mécanique")
                .quantity(2).totalPrice(new BigDecimal("159.98"))
                .status(OrderStatus.CANCELLED).createdAt(LocalDateTime.now()).build();
        when(repository.save(any(Order.class))).thenReturn(cancelled);

        OrderResponse response = service.cancel(1L);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancel() — doit lever une exception si la commande est confirmée")
    void cancel_shouldThrowWhenConfirmed() {
        order.setStatus(OrderStatus.CONFIRMED);
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.cancel(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("confirmée");
    }

    @Test
    @DisplayName("cancel() — doit lever une exception si la commande n'existe pas")
    void cancel_shouldThrowWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancel(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }
}
