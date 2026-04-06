package com.siteshkumar.zomato_clone_backend.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.document.RestaurantDocument;

@Repository
public interface RestaurantSearchRepository extends ElasticsearchRepository<RestaurantDocument, String> {
    Page<RestaurantDocument> findByRestaurantStatusAndNameContainingIgnoreCaseOrRestaurantStatusAndCityContainingIgnoreCase(
            String status1,
            String name,
            String status2,
            String city,
            Pageable pageable);
}
