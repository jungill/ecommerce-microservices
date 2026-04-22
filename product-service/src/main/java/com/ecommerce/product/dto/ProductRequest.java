package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    private String description;
    @NotNull @DecimalMin("0")
    private BigDecimal price;
    @NotNull @Min(0)
    private Integer stock;
    private String category;
}
