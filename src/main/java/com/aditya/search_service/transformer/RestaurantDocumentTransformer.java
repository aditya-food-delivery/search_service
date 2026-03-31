package com.aditya.search_service.transformer;


import com.aditya.contracts.catalog.*;
import com.aditya.search_service.document.RestaurantDetailsDocument;
import com.aditya.search_service.document.RestaurantListingDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestaurantDocumentTransformer {

    public RestaurantListingDocument toListingDocument(
            Restaurant restaurant,
            List<MenuItem> menuItems,
            Map<UUID, String> categoryMap
    ) {

        return RestaurantListingDocument.builder()
                .id(restaurant.getRestaurantId().toString())
                .name(restaurant.getName())
                .description(restaurant.getDescription())

                .city(restaurant.getCity())
                .locality(restaurant.getLocality())

                .rating(restaurant.getRating())
                .deliveryTime(restaurant.getDeliveryTimeMinutes())
                .costForTwo(restaurant.getCostForTwo())
                .pureVeg(restaurant.getPureVeg())
                .isOpen(restaurant.getStatus().equals("OPEN"))

                .cuisines(new ArrayList<>(restaurant.getCuisines()))

                // 🔥 IMPORTANT
                .menuItems(buildMenuItemSearch(menuItems, categoryMap))

                .build();
    }

    private List<RestaurantListingDocument.MenuItemSearch> buildMenuItemSearch(
            List<MenuItem> menuItems,
            Map<UUID, String> categoryMap
    ) {

        return menuItems.stream()
                .filter(item -> !item.isDeleted() &&  !item.getActive() )
                .map(item -> {
                    RestaurantListingDocument.MenuItemSearch search =
                            new RestaurantListingDocument.MenuItemSearch();

                    search.setName(item.getName());
                    search.setVegFlag(item.getVegFlag());

                    // 🔥 Convert categoryId → categoryName
                    search.setCategoryName(categoryMap.get(item.getCategoryId()));

                    return search;
                })
                .toList();
    }
    public RestaurantDetailsDocument toDetailsDocument(
            Restaurant restaurant,
            List<MenuCategory> categories,
            List<MenuItem> items,
            List<ItemVariant> variants,
            List<AddonGroup> addonGroups,
            List<AddonItem> addonItems
    ) {

        return RestaurantDetailsDocument.builder()
                .id(restaurant.getRestaurantId().toString())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .city(restaurant.getCity())
                .locality(restaurant.getLocality())

                .rating(restaurant.getRating())
                .deliveryTime(restaurant.getDeliveryTimeMinutes())
                .costForTwo(restaurant.getCostForTwo())
                .priceRange(restaurant.getPriceRange())

                .coverImageUrl(restaurant.getCoverImageUrl())

                .pureVeg(restaurant.getPureVeg())
                .cuisines(new ArrayList<>(restaurant.getCuisines()))

                // 🔥 CORE TREE BUILDING
                .categories(buildCategoryTree(categories, items, variants, addonGroups, addonItems))

                .build();
    }
    private List<RestaurantDetailsDocument.MenuCategoryDoc> buildCategoryTree(
            List<MenuCategory> categories,
            List<MenuItem> items,
            List<ItemVariant> variants,
            List<AddonGroup> addonGroups,
            List<AddonItem> addonItems
    ) {

        Map<UUID, List<MenuItem>> itemsByCategory =
                items.stream().collect(Collectors.groupingBy(MenuItem::getCategoryId));

        return categories.stream()
                .filter(cat -> !cat.isDeleted())
                .sorted(Comparator.comparing(MenuCategory::getDisplayOrder))
                .map(category -> {

                    List<MenuItem> categoryItems =
                            itemsByCategory.getOrDefault(category.getCategoryId(), List.of());

                    return RestaurantDetailsDocument.MenuCategoryDoc.builder()
                            .id(category.getCategoryId().toString())
                            .name(category.getName())
                            .displayOrder(category.getDisplayOrder())
                            .items(buildItems(categoryItems, variants, addonGroups, addonItems))
                            .build();
                })
                .toList();
    }
    private List<RestaurantDetailsDocument.MenuItemDoc> buildItems(
            List<MenuItem> items,
            List<ItemVariant> variants,
            List<AddonGroup> addonGroups,
            List<AddonItem> addonItems
    ) {

        Map<UUID, List<ItemVariant>> variantMap =
                variants.stream().collect(Collectors.groupingBy(ItemVariant::getItemId));

        Map<UUID, List<AddonGroup>> addonGroupMap =
                addonGroups.stream().collect(Collectors.groupingBy(AddonGroup::getItemId));

        Map<UUID, List<AddonItem>> addonItemMap =
                addonItems.stream().collect(Collectors.groupingBy(AddonItem::getAddonGroupId));

        return items.stream()
                .filter(item -> !item.isDeleted())
                .map(item -> {

                    return RestaurantDetailsDocument.MenuItemDoc.builder()
                            .id(item.getItemId().toString())
                            .name(item.getName())
                            .description(item.getDescription())
                            .imageUrl(item.getImageUrl())

                            .vegFlag(item.getVegFlag())
                            .active(item.getActive())

                            .hasVariants(item.getHasVariants())
                            .hasAddons(item.getHasAddons())

                            .variants(buildVariants(variantMap.get(item.getItemId())))
                            .addonGroups(buildAddonGroups(
                                    addonGroupMap.get(item.getItemId()),
                                    addonItemMap
                            ))
                            .build();
                })
                .toList();
    }
    private List<RestaurantDetailsDocument.VariantDoc> buildVariants(
            List<ItemVariant> variants
    ) {
        if (variants == null) return List.of();

        return variants.stream()
                .filter(v -> !v.isDeleted())
                .map(v -> RestaurantDetailsDocument.VariantDoc.builder()
                        .id(v.getVariantId().toString())
                        .name(v.getName())
                        .price(v.getPrice().doubleValue())
                        .isDefault(v.getIsDefault())
                        .build()
                )
                .toList();
    }
    private List<RestaurantDetailsDocument.AddonGroupDoc> buildAddonGroups(
            List<AddonGroup> groups,
            Map<UUID, List<AddonItem>> addonItemMap
    ) {

        if (groups == null) return List.of();

        return groups.stream()
                .filter(g -> !g.isDeleted())
                .map(group -> {

                    List<AddonItem> items =
                            addonItemMap.getOrDefault(group.getAddonGroupId(), List.of());

                    return RestaurantDetailsDocument.AddonGroupDoc.builder()
                            .id(group.getAddonGroupId().toString())
                            .name(group.getName())
                            .minSelection(group.getMinSelection())
                            .maxSelection(group.getMaxSelection())
                            .required(group.getRequired())
                            .items(buildAddonItems(items))
                            .build();
                })
                .toList();
    }
    private List<RestaurantDetailsDocument.AddonItemDoc> buildAddonItems(
            List<AddonItem> items
    ) {

        return items.stream()
                .filter(i -> !i.isDeleted())
                .map(i -> RestaurantDetailsDocument.AddonItemDoc.builder()
                        .id(i.getAddonItemId().toString())
                        .name(i.getName())
                        .price(i.getPrice().doubleValue())
                        .available(i.isAvailable())
                        .build()
                )
                .toList();
    }
}