package com.aditya.search_service.search;



import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.aditya.search_service.search.dto.RestaurantSearchRequest;

import java.util.ArrayList;
import java.util.List;


import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import com.aditya.search_service.search.dto.RestaurantSearchRequest;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSearchQueryBuilder {

    public static Query build(RestaurantSearchRequest request) {

        List<Query> mustQueries = new ArrayList<>();
        List<Query> filterQueries = new ArrayList<>();

        // 🔍 Search Query (name + cuisines) + fuzziness
        if (request.getQuery() != null && !request.getQuery().isEmpty()) {
            mustQueries.add(MultiMatchQuery.of(m -> m
                    .fields("name^2", "cuisines") // 🔥 boost name
                    .query(request.getQuery())
                    .fuzziness("AUTO") // 🔥 handles typos
            )._toQuery());
        } else {
            // 🔥 If no query → match all
            mustQueries.add(MatchAllQuery.of(m -> m)._toQuery());
        }

        // 🥬 Pure Veg Filter
        if (request.getPureVeg() != null) {
            filterQueries.add(TermQuery.of(t -> t
                    .field("pureVeg")
                    .value(request.getPureVeg())
            )._toQuery());
        }

        // ⭐ Rating Filter
        if (request.getMinRating() != null) {
            filterQueries.add(RangeQuery.of(r -> r
                    .field("rating")
                    .gte(JsonData.of(request.getMinRating()))
            )._toQuery());
        }

        // 🍜 Cuisine Filter
        if (request.getCuisines() != null && !request.getCuisines().isEmpty()) {
            filterQueries.add(TermsQuery.of(t -> t
                    .field("cuisines.keyword") // 🔥 important
                    .terms(v -> v.value(
                            request.getCuisines().stream()
                                    .map(FieldValue::of)
                                    .toList()
                    ))
            )._toQuery());
        }

        // 📍 City Filter (YOUR REQUIREMENT)
        if (request.getCity() != null && !request.getCity().isEmpty()) {
            filterQueries.add(TermQuery.of(t -> t
                    .field("city.keyword") // 🔥 MUST use keyword
                    .value(request.getCity())
            )._toQuery());
        }

        return BoolQuery.of(b -> b
                .must(mustQueries)
                .filter(filterQueries)
        )._toQuery();
    }
}