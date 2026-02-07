package com.wipro.oms.service;

import com.wipro.oms.dto.ProductRequest;
import com.wipro.oms.entity.Product;
import com.wipro.oms.exception.NotFoundException;
import com.wipro.oms.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> list() { return repo.findAll(); }

    public Product get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    public Product create(ProductRequest req) {
        Product p = new Product();
        apply(p, req);
        return repo.save(p);
    }

    public Product update(Long id, ProductRequest req) {
        Product p = get(id);
        apply(p, req);
        return repo.save(p);
    }

    public void delete(Long id) {
        Product p = get(id);
        repo.delete(p);
    }

    private void apply(Product p, ProductRequest req) {
        p.setSku(req.getSku());
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setUnitPrice(req.getUnitPrice());
        p.setStockQty(req.getStockQty());
        p.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
    }
}
