package com.siteshkumar.zomato_clone_backend.repository.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.document.MenuItemDocument;

@Repository
public interface MenuItemSearchRepository extends ElasticsearchRepository<MenuItemDocument, String> {
    
    Page<MenuItemDocument> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
