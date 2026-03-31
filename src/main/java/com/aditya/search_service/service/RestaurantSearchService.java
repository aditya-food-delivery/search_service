package com.aditya.search_service.service;



import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;

import com.aditya.search_service.document.RestaurantListingDocument;
import com.aditya.search_service.search.RestaurantSearchQueryBuilder;
import com.aditya.search_service.search.dto.RestaurantSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final ElasticsearchClient client;

    private static final String INDEX = "restaurant_listing_index";

    public List<RestaurantListingDocument> search(RestaurantSearchRequest request) throws IOException {

        Query query = RestaurantSearchQueryBuilder.build(request);

        // 🔽 Pagination
        int from = request.getPage() * request.getSize();

        // 🔽 Sorting
        SortOptions sort;
        if (request.getSortBy() != null) {
            sort = SortOptionsBuilders.field(f -> f
                    .field(request.getSortBy())
                    .order("asc".equalsIgnoreCase(request.getSortOrder())
                            ? SortOrder.Asc
                            : SortOrder.Desc)
            );
        } else {
            sort = null;
        }

        SearchResponse<RestaurantListingDocument> response = client.search(s -> {
            s.index(INDEX)
                    .query(query)
                    .from(from)
                    .size(request.getSize());

            if (sort != null) {
                s.sort(sort);
            }

            return s;
        }, RestaurantListingDocument.class);

        return response.hits().hits()
                .stream()
                .map(hit -> hit.source())
                .toList();
    }
}