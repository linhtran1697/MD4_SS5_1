package com.example.orders.controller;

import com.example.orders.model.Order;
import com.example.orders.service.OrderService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/searchStatus")
    public List<Order> searchStatus(@RequestParam("status") String status) {
        return orderService.getOrdersByStatus(status);
    }

    @GetMapping("/searchByCustomer")
    public List<Order> searchByCustomer(@RequestParam("customerName") String customerName) {
        return orderService.getOrdersByCustomerName(customerName);
    }
}
