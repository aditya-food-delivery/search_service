package com.aditya.search_service.repository;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.aditya.search_service.document.RestaurantDetailsDocument;
import com.aditya.search_service.document.RestaurantListingDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
@RequiredArgsConstructor
public class RestaurantSearchRepository {

    private final ElasticsearchClient client;

    public void indexListing(RestaurantListingDocument doc) throws IOException {
        client.index(i -> i
                .index("restaurant_listing_index")
                .id(doc.getId())
                .document(doc)
        );
    }

    public void indexDetails(RestaurantDetailsDocument doc) throws IOException {
        client.index(i -> i
                .index("restaurant_details_index")
                .id(doc.getId())
                .document(doc)
        );
    }

    // 🔥 DELETE SUPPORT
    public void deleteById(String id) throws IOException {
        client.delete(d -> d.index("restaurant_listing_index").id(id));
        client.delete(d -> d.index("restaurant_details_index").id(id));
    }
}