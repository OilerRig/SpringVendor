package com.oilerrig.vendor.config;

import com.oilerrig.vendor.data.entities.Product;
import com.oilerrig.vendor.data.entities.ProductDetails;
import com.oilerrig.vendor.data.repository.ProductDetailsRepository;
import com.oilerrig.vendor.data.repository.ProductRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;

@Component
public class DataSeeder {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;

    public DataSeeder(ProductRepository productRepository, ProductDetailsRepository productDetailsRepository) {
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
public void seed() {
    if (productRepository.count() > 0) return;

    Map<UUID, Product> products = Map.of(
        UUID.fromString("00000000-0000-0000-0000-000000000001"), new Product(UUID.fromString("00000000-0000-0000-0000-000000000001"), "Intel i9-13900K", 589.99, 20),
        UUID.fromString("00000000-0000-0000-0000-000000000002"), new Product(UUID.fromString("00000000-0000-0000-0000-000000000002"), "Intel i7-13700K", 409.99, 25),
        UUID.fromString("00000000-0000-0000-0000-000000000003"), new Product(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Intel Arc A770 GPU", 349.99, 12),
        UUID.fromString("00000000-0000-0000-0000-000000000004"), new Product(UUID.fromString("00000000-0000-0000-0000-000000000004"), "Intel NUC 13 Pro Kit", 699.99, 7),
        UUID.fromString("00000000-0000-0000-0000-000000000005"), new Product(UUID.fromString("00000000-0000-0000-0000-000000000005"), "Intel Optane SSD 905P", 499.99, 5),
        UUID.fromString("00000000-0000-0000-0000-000000000006"), new Product(UUID.fromString("00000000-0000-0000-0000-000000000006"), "Intel AX210 Wi-Fi 6E Module", 29.99, 60),
        UUID.fromString("00000000-0000-0000-0000-000000000007"), new Product(UUID.fromString("00000000-0000-0000-0000-000000000007"), "Intel Server Board S2600", 899.99, 3)
    );

    productRepository.saveAll(products.values());

    List<ProductDetails> details = List.of(
        new ProductDetails(UUID.fromString("00000000-0000-0000-0000-000000000001"), Map.of("cores", 24, "clock", "5.8GHz")),
        new ProductDetails(UUID.fromString("00000000-0000-0000-0000-000000000002"), Map.of("cores", 16, "clock", "5.4GHz")),
        new ProductDetails(UUID.fromString("00000000-0000-0000-0000-000000000003"), Map.of("vram", "16GB", "interface", "PCIe 4.0")),
        new ProductDetails(UUID.fromString("00000000-0000-0000-0000-000000000004"), Map.of("cpu", "i9-13900K", "gpu", "RTX 3080")),
        new ProductDetails(UUID.fromString("00000000-0000-0000-0000-000000000005"), Map.of("read", "2600MB/s", "interface", "PCIe 3.0")),
        new ProductDetails(UUID.fromString("00000000-0000-0000-0000-000000000006"), Map.of("wifi", "6E", "interface", "M.2")),
        new ProductDetails(UUID.fromString("00000000-0000-0000-0000-000000000007"), Map.of("socket", "LGA 3647", "chipset", "C622"))
    );

    productDetailsRepository.saveAll(details);
}
}