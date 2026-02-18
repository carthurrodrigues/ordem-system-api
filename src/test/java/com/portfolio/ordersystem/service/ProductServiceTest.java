package com.portfolio.ordersystem.service;

import com.portfolio.ordersystem.dto.ProductDTO.*;
import com.portfolio.ordersystem.entity.Product;
import com.portfolio.ordersystem.exception.ResourceNotFoundException;
import com.portfolio.ordersystem.repository.ProductRepository;
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
@DisplayName("ProductService - Testes Unitários")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Request productRequest;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Notebook Dell")
                .description("Intel i7, 16GB RAM")
                .price(new BigDecimal("4999.99"))
                .stockQuantity(10)
                .build();

        productRequest = new Request();
        productRequest.setName("Notebook Dell");
        productRequest.setDescription("Intel i7, 16GB RAM");
        productRequest.setPrice(new BigDecimal("4999.99"));
        productRequest.setStockQuantity(10);
    }

    @Test
    @DisplayName("Deve retornar todos os produtos")
    void findAll_ShouldReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Response> result = productService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Notebook Dell");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve encontrar produto por ID")
    void findById_WhenProductExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Response result = productService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Notebook Dell");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("4999.99"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void findById_WhenProductNotFound_ShouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void create_ShouldSaveAndReturnProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Response result = productService.create(productRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Notebook Dell");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void update_WhenProductExists_ShouldUpdateAndReturn() {
        Request updateRequest = new Request();
        updateRequest.setName("Notebook Dell Atualizado");
        updateRequest.setDescription("Nova descrição");
        updateRequest.setPrice(new BigDecimal("5499.99"));
        updateRequest.setStockQuantity(8);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.update(1L, updateRequest);

        verify(productRepository, times(1)).save(product);
        assertThat(product.getName()).isEqualTo("Notebook Dell Atualizado");
        assertThat(product.getStockQuantity()).isEqualTo(8);
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void delete_WhenProductExists_ShouldDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("Deve retornar produtos disponíveis em estoque")
    void findAvailable_ShouldReturnProductsWithStock() {
        when(productRepository.findAvailableProducts()).thenReturn(List.of(product));

        List<Response> result = productService.findAvailable();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStockQuantity()).isGreaterThan(0);
    }
}
