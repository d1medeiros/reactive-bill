package com.dmedeiros.reactivemvc.order.handler;

import com.dmedeiros.reactivemvc.order.entity.Order;
import com.dmedeiros.reactivemvc.order.repository.OrderRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class OrderHandler {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Mono<ServerResponse> findAll(ServerRequest serverRequest) {
        return defaultResponse(this.orderRepository.findAll());
    }

    public Mono<ServerResponse> findByOrderId(ServerRequest serverRequest) {
        return this.orderRepository.findByOrderId(serverRequest.pathVariable("orderId"))
                .flatMap(order -> defaultResponse(Mono.just(order)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Order.class)
                .flatMap(this.orderRepository::save)
                .flatMap(order -> ServerResponse.created(URI.create("/order/" + order.getOrderId())).build());
    }

    private static Mono<ServerResponse> defaultResponse(Publisher<Order> orderPublisher) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(orderPublisher, Order.class);
    }

    private static Mono<ServerResponse> defaultWriteResponse(Publisher<Order> orderPublisher) {
        return Mono.from(orderPublisher)
                .flatMap(o -> ServerResponse
                        .created(URI.create("/order/" + o.get_id()))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build()
                );
    }

    private Flux<Order> getMap() {
        return Flux.range(1,6).map(i->new Order("02-"+i, i.toString()));
    }
}
