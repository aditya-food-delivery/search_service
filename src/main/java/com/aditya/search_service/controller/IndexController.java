package com.aditya.search_service.controller;


import com.aditya.contracts.catalog.RestaurantIndexedEvent;
import com.aditya.search_service.service.RestaurantIndexService;
import com.aditya.search_service.service.RestaurantIndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/admin/index")
@RequiredArgsConstructor
public class IndexController {

    private final RestaurantIndexService indexService;
    private final RestaurantIndexingService indexingService;

    @PostMapping("/restaurant")
    public String indexRestaurant(
            @RequestBody RestaurantIndexedEvent event
    ) throws IOException {

        indexingService.indexRestaurant(event);
        return "Restaurant indexed successfully";
    }

    @DeleteMapping("/restaurant/{id}")
    public String deleteRestaurant(@PathVariable String id) throws IOException {

        indexingService.deleteRestaurant(id);
        return "Restaurant deleted successfully";
    }

    // ✅ Safe create
    @PostMapping("/create")
    public String createIndex() throws IOException {
        indexService.createIndexIfNotExists();
        return "Indexes created if not present";
    }

    // ⚠️ FORCE RECREATE (DANGEROUS)
    @PostMapping("/recreate")
    public String recreateIndex() throws IOException {

        indexService.deleteIndex();   // you need to implement this
        indexService.createIndexIfNotExists();

        return "Indexes recreated";
    }
}