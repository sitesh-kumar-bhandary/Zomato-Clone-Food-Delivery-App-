package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.repository.MenuItemRepository;
import com.siteshkumar.zomato_clone_backend.service.InventoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final MenuItemRepository menuItemRepository;

    @Override
    @Transactional
    public void restoreStock(Long menuItemId, Integer quantity) {
        MenuItemEntity item = menuItemRepository
                            .findById(menuItemId)
                            .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        item.setStock(item.getStock() + quantity);
        menuItemRepository.save(item);
    }

    @Override
    @Transactional
    public void deductStock(Long menuItemId, Integer quantity) {

        MenuItemEntity item = menuItemRepository
                .findWithLockById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        if(!item.isAvailable()){
            throw new RuntimeException("Item not available");
        }

        if(item.getStock() < quantity){
            throw new IllegalStateException("Insufficient stock");
        }

        item.setStock(item.getStock() - quantity);
        menuItemRepository.save(item);
    }
}
