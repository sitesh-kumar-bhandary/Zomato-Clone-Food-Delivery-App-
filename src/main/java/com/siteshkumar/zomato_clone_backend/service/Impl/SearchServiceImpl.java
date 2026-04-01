package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.transaction.annotation.Transactional;
import com.siteshkumar.zomato_clone_backend.document.MenuItemDocument;
import com.siteshkumar.zomato_clone_backend.document.RestaurantDocument;
import com.siteshkumar.zomato_clone_backend.dto.SearchResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.restaurant.RestaurantResponseDto;
import com.siteshkumar.zomato_clone_backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {

        private final ElasticsearchOperations elasticsearchOperations;

        @Override
        @Transactional(readOnly = true)
        public SearchResponseDto search(String query, int page, int size) {
                log.info("Search request received. Query: '{}', Page: {}, Size: {}", query, page, size);

                Pageable pageable = PageRequest.of(page, size);

                co.elastic.clients.elasticsearch._types.query_dsl.Query restaurantQuery = co.elastic.clients.elasticsearch._types.query_dsl.Query
                                .of(q -> q
                                                .bool(b -> b
                                                                .must(m -> m
                                                                                .multiMatch(mm -> mm
                                                                                                .query(query)
                                                                                                .fields("name^3",
                                                                                                                "city^2",
                                                                                                                "description")
                                                                                                .fuzziness("AUTO")))
                                                                .filter(f -> f.term(t -> t.field("active").value(true)))
                                                                .filter(f -> f.term(t -> t.field("blocked")
                                                                                .value(false)))));

                NativeQuery restaurantSearchQuery = NativeQuery.builder()
                                .withQuery(restaurantQuery)
                                .withPageable(pageable)
                                .build();

                SearchHits<RestaurantDocument> restaurantHits = elasticsearchOperations.search(restaurantSearchQuery,
                                RestaurantDocument.class);

                List<RestaurantDocument> restaurantList = restaurantHits.stream()
                                .map(SearchHit::getContent)
                                .toList();

                co.elastic.clients.elasticsearch._types.query_dsl.Query menuQuery = co.elastic.clients.elasticsearch._types.query_dsl.Query
                                .of(q -> q
                                                .multiMatch(mm -> mm
                                                                .query(query)
                                                                .fields("name^3", "category^2", "description")
                                                                .fuzziness("AUTO")));

                NativeQuery menuSearchQuery = NativeQuery.builder()
                                .withQuery(menuQuery)
                                .withPageable(pageable)
                                .build();

                SearchHits<MenuItemDocument> menuHits = elasticsearchOperations.search(menuSearchQuery,
                                MenuItemDocument.class);

                List<MenuItemDocument> menuList = menuHits.stream()
                                .map(SearchHit::getContent)
                                .toList();

                log.info("Search results fetched. Restaurants: {}, MenuItems: {}",
                                restaurantHits.getTotalHits(), menuHits.getTotalHits());

                return SearchResponseDto.builder()
                                .restaurants(mapRestaurants(restaurantList))
                                .menuItems(mapMenuItems(menuList))
                                .page(page)
                                .size(size)
                                .totalRestaurants(restaurantHits.getTotalHits())
                                .totalMenuItems(menuHits.getTotalHits())
                                .build();
        }

        private List<RestaurantResponseDto> mapRestaurants(List<RestaurantDocument> restaurants) {
                return restaurants.stream()
                                .map(r -> RestaurantResponseDto.builder()
                                                .id(r.getId() != null ? Long.parseLong(r.getId()) : null)
                                                .name(r.getName())
                                                .city(r.getCity())
                                                .build())
                                .toList();
        }

        private List<MenuItemResponseDto> mapMenuItems(List<MenuItemDocument> items) {
                return items.stream()
                                .map(m -> MenuItemResponseDto.builder()
                                                .id(m.getId() != null ? Long.parseLong(m.getId()) : null)
                                                .name(m.getName())
                                                .price(m.getPrice() != null ? BigDecimal.valueOf(m.getPrice()) : null)
                                                .restaurantName(null)
                                                .build())
                                .toList();
        }

        @Override
        public List<String> suggestRestaurants(String keyword) {

                if (keyword == null || keyword.trim().isEmpty()) {
                        return List.of();
                }

                final String searchKeyword = keyword.trim().toLowerCase();

                if (searchKeyword.length() < 2) {
                        return List.of();
                }

                co.elastic.clients.elasticsearch._types.query_dsl.Query query = co.elastic.clients.elasticsearch._types.query_dsl.Query
                                .of(q -> q
                                                .prefix(p -> p
                                                                .field("name")
                                                                .value(searchKeyword)));

                NativeQuery searchQuery = NativeQuery.builder()
                                .withQuery(query)
                                .withMaxResults(5)
                                .build();

                SearchHits<RestaurantDocument> hits = elasticsearchOperations.search(searchQuery,
                                RestaurantDocument.class);

                return hits.stream()
                                .map(hit -> hit.getContent().getName())
                                .distinct()
                                .toList();
        }
}