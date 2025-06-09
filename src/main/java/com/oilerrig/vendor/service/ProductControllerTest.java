package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.data.dto.ProductResponse;
import com.oilerrig.vendor.data.entities.ProductEntity;
import com.oilerrig.vendor.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ProductService service;

    @Test
    void getAllProducts_returnsJsonList() throws Exception {
        when(service.getAllProducts()).thenReturn(List.of(
            new ProductEntity(1,"A",1.0,1),
            new ProductEntity(2,"B",2.0,2)
        ));

        mvc.perform(get("/products"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].id").value(1))
           .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getProductWithDetails_returnsDto() throws Exception {
        var dto = new ProductResponse();
        dto.setId(9);
        dto.setName("P");
        dto.setPrice(3.3);
        dto.setStock(3);
        dto.setDetails(Map.of("k","v"));
        when(service.getProductWithDetails(9)).thenReturn(dto);

        mvc.perform(get("/products/9/details"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.details.k").value("v"));
    }
}
