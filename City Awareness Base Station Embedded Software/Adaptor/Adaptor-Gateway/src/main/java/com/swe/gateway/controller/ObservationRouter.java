package com.swe.gateway.controller;

import com.swe.gateway.service.ObservationHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
@Configuration
public class ObservationRouter {
    @Bean
    public RouterFunction<ServerResponse> obsRoute(ObservationHandler handler) {
        return RouterFunctions.route(RequestPredicates.GET("/obs/saveBatch")
                .and(accept(MediaType.ALL)), handler::saveObsBatch);
    }


}
