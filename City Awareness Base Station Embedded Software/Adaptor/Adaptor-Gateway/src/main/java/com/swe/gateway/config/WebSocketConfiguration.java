package com.swe.gateway.config;


import com.swe.gateway.service.BleHandler;
import com.swe.gateway.service.RFIDHandler;
import com.swe.gateway.service.RealTimeHandler;
import com.swe.gateway.service.UavHandler;
import com.swe.gateway.util.WebSocketSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class WebSocketConfiguration {

    @Autowired
    @Bean
    public HandlerMapping webSocketMapping(final RealTimeHandler realTimeHandler, final BleHandler bleHandler,
                                           final UavHandler uavHandler, final RFIDHandler rfidHandler) {
        final Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/websocket", realTimeHandler);
        map.put("/ble", bleHandler);
        map.put("/uav", uavHandler);
        map.put("/rfid", rfidHandler);
        final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(map);
        return mapping;
    }
    @Bean
    public ConcurrentHashMap<String, WebSocketSender> senderMap() {
        return new ConcurrentHashMap<>();
    }
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}



