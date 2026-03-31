package com.aditya.search_service.controller;


import com.aditya.search_service.document.RestaurantDetailsDocument;
import com.aditya.search_service.document.RestaurantListingDocument;
import com.aditya.search_service.service.RestaurantDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantDetailsService detailsService;

    @GetMapping("id/{id}")
    public RestaurantDetailsDocument get(@PathVariable String id) throws IOException {
        return detailsService.getById(id);
    }

    @GetMapping("/{city}")
    public List<RestaurantListingDocument> getByCity(
            @PathVariable String city
    ) throws IOException {

        return detailsService.getByCity(city);
    }
}