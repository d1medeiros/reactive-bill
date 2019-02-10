package com.dmedeiros.reactivemvc.conf;

import com.dmedeiros.reactivemvc.order.handler.OrderHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;

@Configuration
public class MainRouter {

    private final OrderHandler orderHandler;
    private RequestPredicate mediaType = RequestPredicates.accept(MediaType.APPLICATION_JSON_UTF8);

    @Autowired
    public MainRouter(OrderHandler orderHandler) {
        this.orderHandler = orderHandler;
    }

    @Bean
    public RouterFunction<?> router() {
        return RouterFunctions.route()
                .GET("/order/all", orderHandler::findAll)
                .GET("/order/{orderId}", orderHandler::findByOrderId)
                .POST("/order", mediaType, orderHandler::create)
                .build();
    }

}
