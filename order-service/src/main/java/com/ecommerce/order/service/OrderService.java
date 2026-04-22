package com.ecommerce.order.service;

import com.ecommerce.order.dto.*;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.model.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;

    public OrderResponse create(OrderRequest req) {
        ProductResponse product = productClient.findById(req.getProductId());
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
        Order order = Order.builder()
                .productId(req.getProductId())
                .productName(product.getName())
                .quantity(req.getQuantity())
                .totalPrice(total)
                .status(OrderStatus.PENDING)
                .build();
        return toResponse(repository.save(order));
    }

    public List<OrderResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public OrderResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Commande introuvable : " + id));
    }

    public OrderResponse confirm(Long id) {
        Order o = getOrThrow(id);
        if (o.getStatus() == OrderStatus.CANCELLED)
            throw new RuntimeException("Impossible de confirmer une commande annulée");
        o.setStatus(OrderStatus.CONFIRMED);
        return toResponse(repository.save(o));
    }

    public OrderResponse cancel(Long id) {
        Order o = getOrThrow(id);
        if (o.getStatus() == OrderStatus.CONFIRMED)
            throw new RuntimeException("Impossible d'annuler une commande confirmée");
        o.setStatus(OrderStatus.CANCELLED);
        return toResponse(repository.save(o));
    }

    private Order getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable : " + id));
    }

    private OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId()).productId(o.getProductId())
                .productName(o.getProductName()).quantity(o.getQuantity())
                .totalPrice(o.getTotalPrice()).status(o.getStatus())
                .createdAt(o.getCreatedAt()).build();
    }
}
