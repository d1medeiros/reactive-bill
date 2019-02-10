package com.dmedeiros.reactivemvc.order.repository;

import com.dmedeiros.reactivemvc.order.entity.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, String> {
    Mono<Order> findByOrderId(String orderId);
}
