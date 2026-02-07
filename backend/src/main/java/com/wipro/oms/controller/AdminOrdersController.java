package com.wipro.oms.controller;

import com.wipro.oms.dto.UpdateStatusRequest;
import com.wipro.oms.entity.OrderStatus;
import com.wipro.oms.entity.PurchaseOrder;
import com.wipro.oms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrdersController {

    private final OrderService service;

    public AdminOrdersController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<PurchaseOrder> list(@RequestParam(required = false) OrderStatus status,
                                    @RequestParam(required = false) String q) {
        return service.allOrders(status, q);
    }

    @PatchMapping("/{orderId}/status")
    public PurchaseOrder changeStatus(@PathVariable Long orderId, @Valid @RequestBody UpdateStatusRequest req) {
        return service.adminUpdateStatus(orderId, req.getStatus());
    }
}
