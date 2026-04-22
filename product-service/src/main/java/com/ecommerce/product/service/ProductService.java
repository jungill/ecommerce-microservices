package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public ProductResponse create(ProductRequest req) {
        Product p = Product.builder()
                .name(req.getName()).description(req.getDescription())
                .price(req.getPrice()).stock(req.getStock())
                .category(req.getCategory()).build();
        return toResponse(repository.save(p));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public ProductResponse findById(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
    }

    public List<ProductResponse> search(String name) {
        return repository.findByNameContainingIgnoreCase(name)
                .stream().map(this::toResponse).toList();
    }

    public ProductResponse update(Long id, ProductRequest req) {
        Product p = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
        p.setName(req.getName()); p.setDescription(req.getDescription());
        p.setPrice(req.getPrice()); p.setStock(req.getStock());
        p.setCategory(req.getCategory());
        return toResponse(repository.save(p));
    }

    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new RuntimeException("Produit introuvable : " + id);
        repository.deleteById(id);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId()).name(p.getName()).description(p.getDescription())
                .price(p.getPrice()).stock(p.getStock()).category(p.getCategory())
                .build();
    }
}
