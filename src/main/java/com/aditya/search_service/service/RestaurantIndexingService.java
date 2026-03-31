package com.aditya.search_service.service;


import com.aditya.contracts.catalog.MenuCategory;
import com.aditya.contracts.catalog.RestaurantIndexedEvent;
import com.aditya.search_service.repository.RestaurantSearchRepository;
import com.aditya.search_service.transformer.RestaurantDocumentTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantIndexingService {

    private final RestaurantDocumentTransformer transformer;
    private final RestaurantSearchRepository repository;

    public void indexRestaurant(RestaurantIndexedEvent event) throws IOException {

        if ("DELETE".equalsIgnoreCase(event.getEventType())) {
            deleteRestaurant(event.getRestaurant().getRestaurantId().toString());
            return;
        }

        // 🔥 TRANSFORM
        var listingDoc = transformer.toListingDocument(
                event.getRestaurant(),
                event.getItems(),
                buildCategoryMap(event.getCategories())
        );

        var detailsDoc = transformer.toDetailsDocument(
                event.getRestaurant(),
                event.getCategories(),
                event.getItems(),
                event.getVariants(),
                event.getAddonGroups(),
                event.getAddonItems()
        );

        // 🔥 INDEX
        repository.indexListing(listingDoc);
        repository.indexDetails(detailsDoc);
    }

    private Map<UUID, String> buildCategoryMap(List<MenuCategory> categories) {
        return categories.stream()
                .collect(Collectors.toMap(
                        MenuCategory::getCategoryId,
                        MenuCategory::getName
                ));
    }

    public void deleteRestaurant(String id) throws IOException{
        repository.deleteById(id);
    }
}