package com.oilerrig.vendor.seeder;

import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.entities.ProductDetailsEntity;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import com.oilerrig.vendor.data.repository.mongo.ProductDetailsRepository;
import com.opencsv.CSVReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CsvSeeder {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;

    public CsvSeeder(ProductRepository productRepository,
                     ProductDetailsRepository productDetailsRepository) {
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
    }

    public void seed() throws Exception {
        String supplierId = System.getenv("SUPPLIER_ID");
        if (supplierId == null || supplierId.isBlank()) {
            System.out.println("SUPPLIER_ID not set. Skipping seed.");
            return;
        }

        String productPath = String.format("data/%s/products.csv", supplierId);
        String detailsPath = String.format("data/%s/details.csv", supplierId);

        if (productRepository.count() == 0) {
            try (var reader = new CSVReader(new InputStreamReader(
                    new ClassPathResource(productPath).getInputStream()))) {
                reader.skip(1);
                List<ProductEntity> products = reader.readAll().stream().map(cols -> {
                    UUID id = UUID.fromString(cols[0]);
                    String name = cols[1];
                    double price = Double.parseDouble(cols[2]);
                    int stock = Integer.parseInt(cols[3]);
                    return new ProductEntity(id, name, price, stock);
                }).collect(Collectors.toList());

                productRepository.saveAll(products);
                System.out.println("Seeded products for supplier: " + supplierId);
            } catch (Exception e) {
                System.out.println("Could not read product CSV: " + e.getMessage());
            }
        }

        if (productDetailsRepository.count() == 0) {
            try (var reader = new CSVReader(new InputStreamReader(
                    new ClassPathResource(detailsPath).getInputStream()))) {
                reader.skip(1);
                List<ProductDetailsEntity> details = reader.readAll().stream().map(cols -> {
                    UUID id = UUID.fromString(cols[0]);
                    Map<String, Object> specs = Arrays.stream(cols[1].split(";"))
                            .map(pair -> pair.split(":", 2))
                            .collect(Collectors.toMap(a -> a[0], a -> a[1]));
                    return new ProductDetailsEntity(id, specs);
                }).collect(Collectors.toList());

                productDetailsRepository.saveAll(details);
                System.out.println("Seeded product details for supplier: " + supplierId);
            } catch (Exception e) {
                System.out.println("Could not read details CSV: " + e.getMessage());
            }
        }
    }
}
