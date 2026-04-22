package com.ecommerce.order.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull(message = "L'id du produit est obligatoire")
    private Long productId;
    @NotNull @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantity;
}
