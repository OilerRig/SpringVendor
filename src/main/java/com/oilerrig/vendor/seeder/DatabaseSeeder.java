package com.oilerrig.vendor.seeder;

import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.data.entities.ProductDetailsEntity;
import com.oilerrig.vendor.data.entities.UserEntity;
import com.oilerrig.vendor.data.repository.jpa.ProductRepository;
import com.oilerrig.vendor.data.repository.jpa.UserRepository;
import com.oilerrig.vendor.data.repository.mongo.ProductDetailsRepository;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
//@Profile("!prod") TODO, WE CAN SEED THIS IN PROD FOR NOW
public class DatabaseSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final UserRepository userRepository;

    @Autowired
    public DatabaseSeeder(ProductRepository productRepository,
                          ProductDetailsRepository productDetailsRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.productDetailsRepository = productDetailsRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String supplierId = "intel";

        String productPath = String.format("data/%s/products.csv", supplierId);
        String detailsPath = String.format("data/%s/details.csv", supplierId);

        if (productRepository.count() == 0) {
            try (var reader = new CSVReader(new InputStreamReader(
                    new ClassPathResource(productPath).getInputStream()))) {
                reader.skip(1);
                List<ProductEntity> products = reader.readAll().stream().map(cols -> {
                    Integer id = Integer.parseInt(cols[0]);
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
                    Integer id = Integer.parseInt(cols[0]);
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

        // SEED USER API KEYS
        UserEntity user = new UserEntity();
        user.setApiKey("TESTKEY");
        userRepository.save(user);
    }
}
