package com.siteshkumar.zomato_clone_backend.service;

public interface InventoryService {

    void restoreStock(Long menuItemId, Integer quantity);
    void deductStock(Long menuItemId, Integer quantity);
    
}
