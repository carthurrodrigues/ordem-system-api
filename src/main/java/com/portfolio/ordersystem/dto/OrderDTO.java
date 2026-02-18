package com.portfolio.ordersystem.dto;

import com.portfolio.ordersystem.entity.Order.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    public static class CreateRequest {
        @NotEmpty(message = "Pedido deve ter ao menos um item")
        @Valid
        private List<OrderItemRequest> items;

        @NotBlank(message = "Endereço de entrega é obrigatório")
        private String deliveryAddress;

        private String notes;
    }

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Produto é obrigatório")
        private Long productId;

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser ao menos 1")
        private Integer quantity;
    }

    @Data
    public static class UpdateStatusRequest {
        @NotNull(message = "Status é obrigatório")
        private OrderStatus status;
    }

    @Data
    public static class Response {
        private Long id;
        private Long userId;
        private String userName;
        private List<OrderItemResponse> items;
        private OrderStatus status;
        private BigDecimal totalAmount;
        private String deliveryAddress;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
