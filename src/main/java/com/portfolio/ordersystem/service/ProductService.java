package com.portfolio.ordersystem.service;

import com.portfolio.ordersystem.dto.ProductDTO.*;
import com.portfolio.ordersystem.entity.Product;
import com.portfolio.ordersystem.exception.ResourceNotFoundException;
import com.portfolio.ordersystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Response> findAll() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<Response> findAvailable() {
        return productRepository.findAvailableProducts()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<Response> search(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Response findById(Long id) {
        Product product = getProductOrThrow(id);
        return toResponse(product);
    }

    @Transactional
    public Response create(Request request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public Response update(Long id, Request request) {
        Product product = getProductOrThrow(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getProductOrThrow(id);
        productRepository.delete(product);
    }

    public Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
    }

    public Response toResponse(Product product) {
        Response response = new Response();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCreatedAt(product.getCreatedAt());
        return response;
    }
}
