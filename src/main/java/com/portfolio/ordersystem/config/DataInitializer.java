package com.portfolio.ordersystem.config;

import com.portfolio.ordersystem.entity.Product;
import com.portfolio.ordersystem.entity.User;
import com.portfolio.ordersystem.entity.User.Role;
import com.portfolio.ordersystem.repository.ProductRepository;
import com.portfolio.ordersystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@ordersystem.com")) {
            User admin = User.builder()
                    .name("Administrador")
                    .email("admin@ordersystem.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            logger.info("Admin criado: admin@ordersystem.com / admin123");
        }

        if (productRepository.count() == 0) {
            productRepository.save(Product.builder().name("Notebook Dell").description("Intel i7, 16GB RAM, SSD 512GB").price(new BigDecimal("4999.99")).stockQuantity(10).build());
            productRepository.save(Product.builder().name("Mouse Logitech MX Master 3").description("Mouse sem fio premium").price(new BigDecimal("499.90")).stockQuantity(50).build());
            productRepository.save(Product.builder().name("Teclado Mecânico Keychron K2").description("Teclado mecânico wireless, switch Brown").price(new BigDecimal("649.00")).stockQuantity(30).build());
            productRepository.save(Product.builder().name("Monitor LG 27''").description("Monitor 4K IPS, 144Hz").price(new BigDecimal("2299.00")).stockQuantity(15).build());
            productRepository.save(Product.builder().name("Headset Sony WH-1000XM5").description("Cancelamento de ruído premium").price(new BigDecimal("1899.00")).stockQuantity(20).build());
            logger.info("5 produtos de exemplo criados.");
        }
    }
}
