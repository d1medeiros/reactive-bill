package com.dmedeiros.reactivemvc.conf;

import com.dmedeiros.reactivemvc.bill.BillHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class BillRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(BillHandler billHandler) {
        RequestPredicate mediaType = RequestPredicates.accept(MediaType.APPLICATION_JSON_UTF8);
        return RouterFunctions.route()
                .GET("/bill", mediaType, billHandler::findAll)
                .GET("/bill/{id}", mediaType, billHandler::findById)
                .GET("/bill/month/{month}", mediaType, billHandler::findByMonth)
                .POST("/bill", mediaType, billHandler::create)
                .PUT("/bill", mediaType, billHandler::update)
                .build();
    }

}
