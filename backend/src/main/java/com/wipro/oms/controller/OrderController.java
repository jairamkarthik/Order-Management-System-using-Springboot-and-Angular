package com.wipro.oms.controller;

import com.wipro.oms.dto.PlaceOrderRequest;
import com.wipro.oms.dto.UpdateOrderRequest;
import com.wipro.oms.entity.OrderStatus;
import com.wipro.oms.entity.PurchaseOrder;
import com.wipro.oms.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // Search: /api/me/orders?status=PLACED&q=sku001
    @GetMapping
    public List<PurchaseOrder> myOrders(@RequestParam(required = false) OrderStatus status,
                                        @RequestParam(required = false) String q) {
        return service.myOrders(status, q);
    }

    @GetMapping("/{orderId}")
    public PurchaseOrder myOrder(@PathVariable Long orderId) {
        return service.myOrder(orderId);
    }

    @PostMapping
    public PurchaseOrder place(@Valid @RequestBody PlaceOrderRequest req) {
        return service.placeMyOrder(req);
    }

    // User can edit only when PLACED
    @PutMapping("/{orderId}")
    public PurchaseOrder update(@PathVariable Long orderId, @Valid @RequestBody UpdateOrderRequest req) {
        return service.updateMyOrder(orderId, req);
    }

    // User cancel rules enforced in service (can't cancel after SHIPPED)
    @PatchMapping("/{orderId}/cancel")
    public PurchaseOrder cancel(@PathVariable Long orderId) {
        return service.cancelMyOrder(orderId);
    }
}
