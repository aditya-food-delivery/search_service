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
public class RestaurantDetailsDocument {

    private String id;

    private String name;
    private String description;

    private String address;
    private String city;
    private String locality;

    private Double rating;
    private Integer deliveryTime;
    private Integer costForTwo;
    private String priceRange;

    private String coverImageUrl;

    private Boolean pureVeg;

    private List<String> cuisines;

    // 🔥 FULL MENU TREE
    private List<MenuCategoryDoc> categories;

    @Data
    @Builder
    @NoArgsConstructor   // 🔥 REQUIRED
    @AllArgsConstructor
    public static class MenuCategoryDoc {
        private String id;
        private String name;
        private Integer displayOrder;

        private List<MenuItemDoc> items;
    }

    @Data
    @Builder
    @NoArgsConstructor   // 🔥 REQUIRED
    @AllArgsConstructor
    public static class MenuItemDoc {
        private String id;
        private String name;
        private String description;
        private String imageUrl;

        private String vegFlag;
        private Boolean active;

        private Boolean hasVariants;
        private Boolean hasAddons;

        private List<VariantDoc> variants;
        private List<AddonGroupDoc> addonGroups;
    }

    @Data
    @Builder
    @NoArgsConstructor   // 🔥 REQUIRED
    @AllArgsConstructor
    public static class VariantDoc {
        private String id;
        private String name;
        private Double price;
        private Boolean isDefault;
    }

    @Data
    @Builder
    @NoArgsConstructor   // 🔥 REQUIRED
    @AllArgsConstructor
    public static class AddonGroupDoc {
        private String id;
        private String name;
        private Integer minSelection;
        private Integer maxSelection;
        private Boolean required;

        private List<AddonItemDoc> items;
    }

    @Data
    @Builder
    @NoArgsConstructor   // 🔥 REQUIRED
    @AllArgsConstructor
    public static class AddonItemDoc {
        private String id;
        private String name;
        private Double price;
        private Boolean available;
    }
}