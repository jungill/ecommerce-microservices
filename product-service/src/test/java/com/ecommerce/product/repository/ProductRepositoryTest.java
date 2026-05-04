package com.ecommerce.product.repository;

import com.ecommerce.product.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;INIT=CREATE SCHEMA IF NOT EXISTS product",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.jpa.properties.hibernate.default_schema=product",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
@DisplayName("ProductRepository — Tests JPA")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        repository.save(Product.builder()
                .name("Clavier mécanique")
                .price(new BigDecimal("79.99"))
                .stock(15)
                .category("Informatique")
                .build());

        repository.save(Product.builder()
                .name("Clavier sans fil")
                .price(new BigDecimal("49.99"))
                .stock(8)
                .category("Informatique")
                .build());

        repository.save(Product.builder()
                .name("Souris gaming")
                .price(new BigDecimal("39.99"))
                .stock(20)
                .category("Informatique")
                .build());
    }

    @Test
    @DisplayName("save() — doit persister un produit avec un id généré")
    void save_shouldPersistProduct() {
        Product p = Product.builder()
                .name("Écran 4K")
                .price(new BigDecimal("399.99"))
                .stock(5)
                .build();

        Product saved = repository.save(p);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Écran 4K");
    }

    @Test
    @DisplayName("findAll() — doit retourner tous les produits")
    void findAll_shouldReturnAllProducts() {
        List<Product> products = repository.findAll();
        assertThat(products).hasSize(3);
    }

    @Test
    @DisplayName("findById() — doit retourner le bon produit")
    void findById_shouldReturnProduct() {
        Product saved = repository.save(Product.builder()
                .name("Test").price(BigDecimal.ONE).stock(1).build());

        Optional<Product> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test");
    }

    @Test
    @DisplayName("findById() — doit retourner Optional vide si inexistant")
    void findById_shouldReturnEmptyWhenNotFound() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase() — doit trouver par nom partiel")
    void findByName_shouldReturnMatchingProducts() {
        List<Product> result = repository.findByNameContainingIgnoreCase("clavier");
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase() — insensible à la casse")
    void findByName_shouldBeCaseInsensitive() {
        List<Product> result = repository.findByNameContainingIgnoreCase("CLAVIER");
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase() — retourne vide si aucun résultat")
    void findByName_shouldReturnEmptyWhenNoMatch() {
        assertThat(repository.findByNameContainingIgnoreCase("xyz")).isEmpty();
    }

    @Test
    @DisplayName("deleteById() — doit supprimer le produit")
    void deleteById_shouldRemoveProduct() {
        Product saved = repository.save(Product.builder()
                .name("À supprimer").price(BigDecimal.ONE).stock(1).build());
        Long id = saved.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("existsById() — doit retourner true si le produit existe")
    void existsById_shouldReturnTrue() {
        Product saved = repository.save(Product.builder()
                .name("Existe").price(BigDecimal.ONE).stock(1).build());
        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    @DisplayName("existsById() — doit retourner false si le produit n'existe pas")
    void existsById_shouldReturnFalse() {
        assertThat(repository.existsById(999L)).isFalse();
    }
}