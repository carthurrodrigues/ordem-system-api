package com.portfolio.ordersystem.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Nome é obrigatório")
        private String name;

        private String description;

        @NotNull(message = "Preço é obrigatório")
        @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
        private BigDecimal price;

        @NotNull(message = "Quantidade em estoque é obrigatória")
        @Min(value = 0, message = "Quantidade não pode ser negativa")
        private Integer stockQuantity;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private LocalDateTime createdAt;
    }
}
