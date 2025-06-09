package com.oilerrig.vendor.controller;

import com.oilerrig.vendor.data.dto.OrderRequest;
import com.oilerrig.vendor.data.dto.OrderResponse;
import com.oilerrig.vendor.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private OrderService service;

    @Test
    void placeOrder_endpoint() throws Exception {
        var req = new OrderRequest();
        req.setProductId(1);
        req.setQuantity(2);

        var resp = new OrderResponse();
        resp.setProductId(1);
        resp.setQuantity(2);
        when(service.placeOrder(1,2)).thenReturn(resp);

        mvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\":1,\"quantity\":2}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void cancelOrder_endpoint() throws Exception {
        var id = UUID.randomUUID();
        var resp = new OrderResponse();
        resp.setId(id);
        resp.setStatus("CANCELLED");
        when(service.revertOrder(id)).thenReturn(resp);

        mvc.perform(delete("/orders/" + id))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
