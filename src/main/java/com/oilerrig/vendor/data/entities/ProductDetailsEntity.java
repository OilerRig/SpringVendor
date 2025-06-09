package com.oilerrig.vendor.data.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.UUID;

@Document(collection = "productDetails")
public class ProductDetailsEntity {

    @Id
    private Integer id;
    private Map<String, Object> specs;

    public ProductDetailsEntity() {}

    public ProductDetailsEntity(Integer id, Map<String, Object> specs) {
        this.id = id;
        this.specs = specs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Map<String, Object> getSpecs() {
        return specs;
    }

    public void setSpecs(Map<String, Object> specs) {
        this.specs = specs;
    }
}
