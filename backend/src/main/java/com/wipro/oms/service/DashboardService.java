package com.wipro.oms.service;

import com.wipro.oms.entity.OrderStatus;
import com.wipro.oms.repository.OrderRepository;
import com.wipro.oms.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public DashboardService(OrderRepository orderRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    public Map<String, Object> stats() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalProducts", productRepo.count());
        m.put("totalOrders", orderRepo.count());
        m.put("activeOrders", orderRepo.countByStatus(OrderStatus.PLACED) + orderRepo.countByStatus(OrderStatus.SHIPPED));
        m.put("placed", orderRepo.countByStatus(OrderStatus.PLACED));
        m.put("shipped", orderRepo.countByStatus(OrderStatus.SHIPPED));
        m.put("delivered", orderRepo.countByStatus(OrderStatus.DELIVERED));
        return m;
    }
}
