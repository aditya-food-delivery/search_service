package com.aditya.search_service.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.aditya.search_service.document.RestaurantDetailsDocument;
import com.aditya.search_service.document.RestaurantListingDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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

    public List<RestaurantListingDocument> getByCity(String city) throws IOException {
        System.out.println(city);
        Query query = TermQuery.of(t -> t
                .field("city")   // 🔥 exact match
                .value(city)
        )._toQuery();

        SearchResponse<RestaurantListingDocument> response = client.search(s -> s
                        .index("restaurant_listing_index")
                        .query(query)
                        .size(20),
                RestaurantListingDocument.class
        );

        return response.hits().hits()
                .stream()
                .map(hit -> hit.source())
                .toList();
    }
}