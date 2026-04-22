package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<OrderResponse> findAll() { return service.findAll(); }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) { return service.findById(id); }

    @PatchMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable Long id) { return service.confirm(id); }

    @PatchMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id) { return service.cancel(id); }
}
