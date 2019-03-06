package com.dmedeiros.reactivemvc.conf;

import com.dmedeiros.reactivemvc.bill.BillHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.*;

import java.util.Arrays;

@Configuration
@EnableWebFlux
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
                .DELETE("/bill/{id}", mediaType, billHandler::remove)
                .build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setMaxAge(8000L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}
