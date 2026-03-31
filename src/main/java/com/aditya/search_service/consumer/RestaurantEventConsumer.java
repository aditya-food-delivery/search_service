package com.aditya.search_service.consumer;

import com.aditya.contracts.catalog.RestaurantIndexedEvent;
import com.aditya.search_service.config.RabbitMQConfig;
import com.aditya.search_service.service.RestaurantIndexService;
import com.aditya.search_service.service.RestaurantIndexingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RestaurantEventConsumer {

    private final RestaurantIndexingService indexingService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void consume(RestaurantIndexedEvent event) throws IOException {
        System.out.println("Ohh i get it ");

        if ("DELETE".equals(event.getEventType())) {
            // TODO: delete from ES
            return;
        }

        indexingService.indexRestaurant(event);
    }
}
