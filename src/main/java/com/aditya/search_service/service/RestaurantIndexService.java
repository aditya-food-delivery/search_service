package com.aditya.search_service.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.analysis.TokenChar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RestaurantIndexService {

    private final ElasticsearchClient client;

    // 🔥 CREATE BOTH INDEXES
    public void createIndexIfNotExists() throws IOException {

        boolean listingExists = client.indices()
                .exists(e -> e.index("restaurant_listing_index"))
                .value();

        if (!listingExists) {
            createListingIndex();
        }

        boolean detailsExists = client.indices()
                .exists(e -> e.index("restaurant_details_index"))
                .value();

        if (!detailsExists) {
            createDetailsIndex();
        }
    }

    // ===============================
    // 🔍 LISTING INDEX (SEARCH HEAVY)
    // ===============================
    public void createListingIndex() throws IOException {

        client.indices().create(c -> c
                .index("restaurant_listing_index")

                // ⚙️ SETTINGS (IMPORTANT)
                .settings(s -> s
                        .numberOfShards("1")
                        .numberOfReplicas("1")

                        // 🔥 AUTOCOMPLETE ANALYZER
                        .analysis(a -> a

                                // ✅ TOKENIZER DEFINITION
                                .tokenizer("autocomplete_tokenizer", t -> t
                                        .definition(td -> td
                                                .edgeNgram(eng -> eng
                                                        .minGram(2)
                                                        .maxGram(20)
                                                        .tokenChars(TokenChar.Letter, TokenChar.Digit)
                                                )
                                        )
                                )

                                // ✅ ANALYZER DEFINITION
                                .analyzer("autocomplete", an -> an
                                        .custom(ca -> ca
                                                .tokenizer("autocomplete_tokenizer")
                                                .filter("lowercase")
                                        )
                                )
                        )
                )

                // 🧠 MAPPINGS
                .mappings(m -> m

                        // ===== BASIC =====
                        .properties("id", p -> p.keyword(k -> k))

                        // 🔍 NAME (Search + Sort + Autocomplete)
                        .properties("name", p -> p.text(t -> t
                                .analyzer("standard")
                                .fields("keyword", f -> f.keyword(k -> k))
                                .fields("autocomplete", f -> f.text(tt -> tt.analyzer("autocomplete")))
                        ))

                        .properties("description", p -> p.text(t -> t))

                        // 📍 LOCATION
                        .properties("city", p -> p.keyword(k -> k))
                        .properties("locality", p -> p.keyword(k -> k))

                        // ⭐ FILTER + SORT
                        .properties("rating", p -> p.double_(d -> d))
                        .properties("deliveryTime", p -> p.integer(i -> i))
                        .properties("costForTwo", p -> p.integer(i -> i))

                        .properties("pureVeg", p -> p.boolean_(b -> b))
                        .properties("isOpen", p -> p.boolean_(b -> b))

                        .properties("cuisines", p -> p.keyword(k -> k))

                        // 🔥 MENU ITEMS (NESTED SEARCH)
                        .properties("menuItems", p -> p.nested(n -> n
                                .properties("name", np -> np.text(t -> t
                                        .fields("autocomplete", f -> f.text(tt -> tt.analyzer("autocomplete")))
                                ))
                                .properties("categoryName", np -> np.keyword(k -> k))
                                .properties("vegFlag", np -> np.keyword(k -> k))
                        ))
                )
        );
    }

    // ===============================
    // 📦 DETAILS INDEX (FETCH ONLY)
    // ===============================
    public void createDetailsIndex() throws IOException {

        client.indices().create(c -> c
                .index("restaurant_details_index")

                .settings(s -> s
                        .numberOfShards("1")
                        .numberOfReplicas("1")
                )

                .mappings(m -> m

                        .properties("id", p -> p.keyword(k -> k))

                        .properties("name", p -> p.text(t -> t))
                        .properties("description", p -> p.text(t -> t))

                        .properties("address", p -> p.text(t -> t))
                        .properties("city", p -> p.keyword(k -> k))
                        .properties("locality", p -> p.keyword(k -> k))

                        .properties("rating", p -> p.double_(d -> d))
                        .properties("deliveryTime", p -> p.integer(i -> i))
                        .properties("costForTwo", p -> p.integer(i -> i))
                        .properties("priceRange", p -> p.keyword(k -> k))

                        .properties("coverImageUrl", p -> p.keyword(k -> k))

                        .properties("pureVeg", p -> p.boolean_(b -> b))

                        .properties("cuisines", p -> p.keyword(k -> k))

                        // 🔥 FULL MENU TREE
                        .properties("categories", p -> p.nested(n -> n

                                .properties("id", np -> np.keyword(k -> k))
                                .properties("name", np -> np.text(t -> t))
                                .properties("displayOrder", np -> np.integer(i -> i))

                                .properties("items", np -> np.nested(n2 -> n2

                                        .properties("id", ip -> ip.keyword(k -> k))
                                        .properties("name", ip -> ip.text(t -> t))
                                        .properties("description", ip -> ip.text(t -> t))
                                        .properties("imageUrl", ip -> ip.keyword(k -> k))

                                        .properties("vegFlag", ip -> ip.keyword(k -> k))
                                        .properties("active", ip -> ip.boolean_(b -> b))

                                        .properties("hasVariants", ip -> ip.boolean_(b -> b))
                                        .properties("hasAddons", ip -> ip.boolean_(b -> b))

                                        // 🔥 VARIANTS
                                        .properties("variants", ip -> ip.nested(v -> v
                                                .properties("id", vp -> vp.keyword(k -> k))
                                                .properties("name", vp -> vp.keyword(k -> k))
                                                .properties("price", vp -> vp.double_(d -> d))
                                                .properties("isDefault", vp -> vp.boolean_(b -> b))
                                        ))

                                        // 🔥 ADDONS
                                        .properties("addonGroups", ip -> ip.nested(ag -> ag
                                                .properties("id", agp -> agp.keyword(k -> k))
                                                .properties("name", agp -> agp.keyword(k -> k))
                                                .properties("minSelection", agp -> agp.integer(i -> i))
                                                .properties("maxSelection", agp -> agp.integer(i -> i))
                                                .properties("required", agp -> agp.boolean_(b -> b))

                                                .properties("items", agp -> agp.nested(ai -> ai
                                                        .properties("id", aip -> aip.keyword(k -> k))
                                                        .properties("name", aip -> aip.keyword(k -> k))
                                                        .properties("price", aip -> aip.double_(d -> d))
                                                        .properties("available", aip -> aip.boolean_(b -> b))
                                                ))
                                        ))
                                ))
                        ))
                )
        );
    }

    public void deleteIndex() throws IOException {

        client.indices().delete(d -> d.index("restaurant_listing_index"));
        client.indices().delete(d -> d.index("restaurant_details_index"));
    }
}