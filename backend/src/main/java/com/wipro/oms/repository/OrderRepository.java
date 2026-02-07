package com.wipro.oms.repository;

import com.wipro.oms.entity.OrderStatus;
import com.wipro.oms.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<PurchaseOrder, Long> {
    long countByStatus(OrderStatus status);

    @Query("""
        select distinct o from PurchaseOrder o
        left join o.items i
        left join i.product p
        where o.placedBy.username = :username
          and (:status is null or o.status = :status)
          and (
               :q is null
            or cast(o.orderId as string) like concat('%', :q, '%')
            or lower(p.name) like lower(concat('%', :q, '%'))
            or lower(p.sku) like lower(concat('%', :q, '%'))
          )
        order by o.createdAt desc
        """)
    java.util.List<PurchaseOrder> searchMyOrders(@Param("username") String username,
                                                @Param("status") OrderStatus status,
                                                @Param("q") String q);

    @Query("""
        select distinct o from PurchaseOrder o
        left join o.items i
        left join i.product p
        where (:status is null or o.status = :status)
          and (
               :q is null
            or cast(o.orderId as string) like concat('%', :q, '%')
            or lower(p.name) like lower(concat('%', :q, '%'))
            or lower(p.sku) like lower(concat('%', :q, '%'))
            or lower(o.placedBy.username) like lower(concat('%', :q, '%'))
          )
        order by o.createdAt desc
        """)
    java.util.List<PurchaseOrder> searchAllOrders(@Param("status") OrderStatus status,
                                                 @Param("q") String q);
}

