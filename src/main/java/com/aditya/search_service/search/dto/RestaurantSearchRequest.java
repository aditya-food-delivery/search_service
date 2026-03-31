package com.aditya.search_service.search.dto;


import lombok.Data;

import java.util.List;

@Data
public class RestaurantSearchRequest {

    private String query;

    private Boolean pureVeg;
    private Double minRating;
    private List<String> cuisines;
    private String city;
    private String sortBy; // rating / deliveryTime
    private String sortOrder; // asc / desc

    private int page;
    private int size;
}