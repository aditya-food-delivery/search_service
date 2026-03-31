package com.aditya.search_service.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListingDocument {

    private String id;

    // 🔍 Search fields
    private String name;
    private String description;

    // 📍 Location
    private String city;
    private String locality;

    // ⭐ Filters & Sorting
    private Double rating;
    private Integer deliveryTime;
    private Integer costForTwo;
    private Boolean pureVeg;
    private Boolean isOpen;

    private List<String> cuisines;

    // 🔥 SEARCH MAGIC (VERY IMPORTANT)
    private List<MenuItemSearch> menuItems;

    @Data
    public static class MenuItemSearch {
        private String name;
        private String categoryName;
        private String vegFlag;
    }
}