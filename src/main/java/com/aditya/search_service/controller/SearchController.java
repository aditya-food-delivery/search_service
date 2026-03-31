package com.aditya.search_service.controller;



import com.aditya.search_service.document.RestaurantDetailsDocument;
import com.aditya.search_service.document.RestaurantListingDocument;
import com.aditya.search_service.search.dto.RestaurantSearchRequest;
import com.aditya.search_service.service.RestaurantSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/search/restaurants")
@RequiredArgsConstructor
public class SearchController {

    private final RestaurantSearchService searchService;

    @PostMapping
    public List<RestaurantListingDocument> search(@RequestBody RestaurantSearchRequest request) throws IOException {
        return searchService.search(request);
    }
}