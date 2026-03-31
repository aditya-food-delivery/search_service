package com.aditya.search_service.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.aditya.search_service.document.RestaurantDetailsDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RestaurantDetailsService {

    private final ElasticsearchClient client;

    public RestaurantDetailsDocument getById(String id) throws IOException {

        var response = client.get(g -> g
                        .index("restaurant_details_index")
                        .id(id),
                RestaurantDetailsDocument.class
        );

        return response.source();
    }
}