package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService — Tests unitaires")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private Product product;
    private ProductRequest request;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Clavier mécanique")
                .description("RGB switches bleus")
                .price(new BigDecimal("79.99"))
                .stock(15)
                .category("Informatique")
                .build();

        request = new ProductRequest();
        request.setName("Clavier mécanique");
        request.setDescription("RGB switches bleus");
        request.setPrice(new BigDecimal("79.99"));
        request.setStock(15);
        request.setCategory("Informatique");
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("create() — doit retourner un ProductResponse avec les bons champs")
    void create_shouldReturnProductResponse() {
        when(repository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = service.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Clavier mécanique");
        assertThat(response.getPrice()).isEqualByComparingTo("79.99");
        assertThat(response.getStock()).isEqualTo(15);
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("create() — doit appeler repository.save() une seule fois")
    void create_shouldCallSaveOnce() {
        when(repository.save(any(Product.class))).thenReturn(product);

        service.create(request);

        verify(repository, times(1)).save(any(Product.class));
    }

    // ── FIND ALL ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll() — doit retourner la liste complète des produits")
    void findAll_shouldReturnAllProducts() {
        Product product2 = Product.builder()
                .id(2L).name("Souris").price(new BigDecimal("29.99")).stock(20).build();
        when(repository.findAll()).thenReturn(List.of(product, product2));

        List<ProductResponse> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Clavier mécanique");
        assertThat(result.get(1).getName()).isEqualTo("Souris");
    }

    @Test
    @DisplayName("findAll() — doit retourner une liste vide si aucun produit")
    void findAll_shouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<ProductResponse> result = service.findAll();

        assertThat(result).isEmpty();
    }

    // ── FIND BY ID ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById() — doit retourner le produit correspondant")
    void findById_shouldReturnProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = service.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Clavier mécanique");
    }

    @Test
    @DisplayName("findById() — doit lever une exception si le produit n'existe pas")
    void findById_shouldThrowWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("update() — doit mettre à jour et retourner le produit modifié")
    void update_shouldReturnUpdatedProduct() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        request.setName("Clavier V2");
        request.setPrice(new BigDecimal("89.99"));

        ProductResponse response = service.update(1L, request);

        assertThat(response).isNotNull();
        verify(repository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("update() — doit lever une exception si le produit n'existe pas")
    void update_shouldThrowWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete() — doit supprimer le produit existant")
    void delete_shouldDeleteProduct() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete() — doit lever une exception si le produit n'existe pas")
    void delete_shouldThrowWhenNotFound() {
        when(repository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("999");

        verify(repository, never()).deleteById(any());
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("search() — doit retourner les produits correspondant au nom")
    void search_shouldReturnMatchingProducts() {
        when(repository.findByNameContainingIgnoreCase("clavier"))
                .thenReturn(List.of(product));

        List<ProductResponse> result = service.search("clavier");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Clavier mécanique");
    }

    @Test
    @DisplayName("search() — doit retourner une liste vide si aucun résultat")
    void search_shouldReturnEmptyWhenNoMatch() {
        when(repository.findByNameContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        List<ProductResponse> result = service.search("xyz");

        assertThat(result).isEmpty();
    }
}
