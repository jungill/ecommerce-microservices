package com.ecommerce.order.service;

import com.ecommerce.order.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${product.service.url:http://localhost:8081}")
    private String productServiceUrl;

    public ProductResponse findById(Long id) {
        return webClientBuilder.build()
                .get()
                .uri(productServiceUrl + "/api/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }
}
