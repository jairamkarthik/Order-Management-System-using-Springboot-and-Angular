package com.wipro.oms.service;

import com.wipro.oms.dto.AddressDto;
import com.wipro.oms.dto.PlaceOrderRequest;
import com.wipro.oms.dto.UpdateOrderRequest;
import com.wipro.oms.entity.*;
import com.wipro.oms.exception.BadRequestException;
import com.wipro.oms.exception.NotFoundException;
import com.wipro.oms.repository.AppUserRepository;
import com.wipro.oms.repository.OrderRepository;
import com.wipro.oms.repository.ProductRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final AppUserRepository userRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo, AppUserRepository userRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    private String currentUsername() {
        var a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null || a.getPrincipal() == null) throw new BadRequestException("Unauthenticated");
        return a.getPrincipal().toString();
    }

    private AppUser currentUser() {
        return userRepo.findByUsername(currentUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));
    }


    public List<PurchaseOrder> myOrders(OrderStatus status, String q) {
        String keyword = (q == null || q.isBlank()) ? null : q.trim();
        return orderRepo.searchMyOrders(currentUsername(), status, keyword);
    }

    public PurchaseOrder myOrder(Long orderId) {
        PurchaseOrder o = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        if (!o.getPlacedBy().getUsername().equalsIgnoreCase(currentUsername())) {
            throw new NotFoundException("Order not found: " + orderId);
        }
        return o;
    }

    @Transactional
    public PurchaseOrder placeMyOrder(PlaceOrderRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new BadRequestException("Order must have at least one item");
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setPlacedBy(currentUser());
        order.setShippingAddress(toEntity(req.getAddress()));

        applyItemsToOrder(order, req.getItems(), true);

        return orderRepo.save(order);
    }

    @Transactional
    public PurchaseOrder updateMyOrder(Long orderId, UpdateOrderRequest req) {
        PurchaseOrder o = myOrder(orderId);

        if (o.getStatus() != OrderStatus.PLACED) {
            throw new BadRequestException("Order cannot be edited once it is " + o.getStatus());
        }

        for (OrderItem old : o.getItems()) {
            Product p = old.getProduct();
            p.setStockQty(p.getStockQty() + old.getQty());
            productRepo.save(p);
        }
        o.getItems().clear();

        applyItemsToOrder(o, req.getItems(), false);

        o.setShippingAddress(toEntity(req.getAddress()));
        return orderRepo.save(o);
    }

    @Transactional
    public PurchaseOrder cancelMyOrder(Long orderId) {
        PurchaseOrder o = myOrder(orderId);

        if (o.getStatus() == OrderStatus.SHIPPED || o.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot cancel once order is " + o.getStatus());
        }
        if (o.getStatus() == OrderStatus.CANCELLED) {
            return o;
        }

        for (OrderItem it : o.getItems()) {
            Product p = it.getProduct();
            p.setStockQty(p.getStockQty() + it.getQty());
            productRepo.save(p);
        }

        o.setStatus(OrderStatus.CANCELLED);
        return orderRepo.save(o);
    }



    public List<PurchaseOrder> allOrders(OrderStatus status, String q) {
        String keyword = (q == null || q.isBlank()) ? null : q.trim();
        return orderRepo.searchAllOrders(status, keyword);
    }

    @Transactional
    public PurchaseOrder adminUpdateStatus(Long orderId, OrderStatus status) {
        PurchaseOrder o = orderRepo.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));

        if (o.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot change status of a CANCELLED order");
        }
        if (o.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot change status of a DELIVERED order");
        }

        // simple forward-only rule
        if (o.getStatus() == OrderStatus.SHIPPED && status == OrderStatus.PLACED) {
            throw new BadRequestException("Cannot move status backwards");
        }
        if (o.getStatus() == OrderStatus.DELIVERED && status != OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot move status backwards");
        }

        o.setStatus(status);
        return orderRepo.save(o);
    }



    private ShippingAddress toEntity(AddressDto dto) {
        ShippingAddress a = new ShippingAddress();
        a.setFullName(dto.getFullName());
        a.setPhone(dto.getPhone());
        a.setLine1(dto.getLine1());
        a.setLine2(dto.getLine2());
        a.setCity(dto.getCity());
        a.setState(dto.getState());
        a.setPincode(dto.getPincode());
        a.setCountry(dto.getCountry());
        return a;
    }

    private void applyItemsToOrder(PurchaseOrder order, List<PlaceOrderRequest.Item> items, boolean isNewOrder) {
        for (var it : items) {
            Product p = productRepo.findById(it.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + it.getProductId()));

            if (Boolean.FALSE.equals(p.getIsActive())) {
                throw new BadRequestException("Product is inactive: " + p.getProductId());
            }

            if (p.getStockQty() < it.getQty()) {
                throw new BadRequestException("Insufficient stock for productId=" + p.getProductId());
            }

            p.setStockQty(p.getStockQty() - it.getQty());
            productRepo.save(p);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQty(it.getQty());
            oi.setUnitPriceAtPurchase(p.getUnitPrice());

            order.getItems().add(oi);
        }
    }
}
