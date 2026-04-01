package com.siteshkumar.zomato_clone_backend.service;

import java.util.List;

import com.siteshkumar.zomato_clone_backend.dto.SearchResponseDto;

public interface SearchService {

    SearchResponseDto search(String query, int page, int size);
    List<String> suggestRestaurants(String keyword);
}
