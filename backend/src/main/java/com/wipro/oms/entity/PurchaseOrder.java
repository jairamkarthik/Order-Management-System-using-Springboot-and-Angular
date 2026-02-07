package com.wipro.oms.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private AppUser placedBy;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fullName", column = @Column(name = "ship_full_name", nullable = false, length = 120)),
            @AttributeOverride(name = "phone", column = @Column(name = "ship_phone", nullable = false, length = 20)),
            @AttributeOverride(name = "line1", column = @Column(name = "ship_line1", nullable = false, length = 200)),
            @AttributeOverride(name = "line2", column = @Column(name = "ship_line2", length = 200)),
            @AttributeOverride(name = "city", column = @Column(name = "ship_city", nullable = false, length = 100)),
            @AttributeOverride(name = "state", column = @Column(name = "ship_state", nullable = false, length = 100)),
            @AttributeOverride(name = "pincode", column = @Column(name = "ship_postal_code", nullable = false, length = 20)),
            @AttributeOverride(name = "country", column = @Column(name = "ship_country", nullable = false, length = 80))
    })
    private ShippingAddress shippingAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PLACED;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    // ===== Getters & Setters =====

    public Long getOrderId() {
        return orderId;
    }

    public AppUser getPlacedBy() {
        return placedBy;
    }

    public void setPlacedBy(AppUser placedBy) {
        this.placedBy = placedBy;
    }

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}