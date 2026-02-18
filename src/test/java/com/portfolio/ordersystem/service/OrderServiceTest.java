package com.portfolio.ordersystem.service;

import com.portfolio.ordersystem.dto.OrderDTO.*;
import com.portfolio.ordersystem.entity.*;
import com.portfolio.ordersystem.entity.Order.OrderStatus;
import com.portfolio.ordersystem.exception.BusinessException;
import com.portfolio.ordersystem.exception.ResourceNotFoundException;
import com.portfolio.ordersystem.repository.OrderRepository;
import com.portfolio.ordersystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService - Testes Unitários")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .role(User.Role.USER)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Notebook Dell")
                .price(new BigDecimal("4999.99"))
                .stockQuantity(10)
                .build();
    }

    @Test
    @DisplayName("Deve criar pedido com sucesso e deduzir estoque")
    void createOrder_ShouldCreateOrderAndReduceStock() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateRequest request = new CreateRequest();
        request.setItems(List.of(itemRequest));
        request.setDeliveryAddress("Rua das Flores, 123");

        Order savedOrder = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("9999.98"))
                .deliveryAddress("Rua das Flores, 123")
                .items(new ArrayList<>())
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .order(savedOrder)
                .product(product)
                .quantity(2)
                .unitPrice(product.getPrice())
                .build();

        savedOrder.getItems().add(orderItem);

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(productService.getProductOrThrow(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Response result = orderService.createOrder(request, "joao@email.com");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(product.getStockQuantity()).isEqualTo(8); // 10 - 2
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque insuficiente")
    void createOrder_WhenInsufficientStock_ShouldThrowException() {
        product.setStockQuantity(1); // apenas 1 no estoque

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(5); // pedindo 5

        CreateRequest request = new CreateRequest();
        request.setItems(List.of(itemRequest));
        request.setDeliveryAddress("Rua das Flores, 123");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(productService.getProductOrThrow(1L)).thenReturn(product);

        assertThatThrownBy(() -> orderService.createOrder(request, "joao@email.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Estoque insuficiente");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar status de pedido cancelado")
    void updateStatus_WhenOrderCancelled_ShouldThrowException() {
        Order cancelledOrder = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.CANCELLED)
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(cancelledOrder));

        UpdateStatusRequest updateRequest = new UpdateStatusRequest();
        updateRequest.setStatus(OrderStatus.CONFIRMED);

        assertThatThrownBy(() -> orderService.updateStatus(1L, updateRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CANCELLED");
    }

    @Test
    @DisplayName("Deve retornar pedidos do usuário logado")
    void findMyOrders_ShouldReturnUserOrders() {
        Order order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("4999.99"))
                .items(new ArrayList<>())
                .build();

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));

        List<Response> result = orderService.findMyOrders("joao@email.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pedido não encontrado")
    void findById_WhenOrderNotFound_ShouldThrowException() {
        when(orderRepository.findByIdWithItems(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(99L, "joao@email.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
