package com.siteshkumar.zomato_clone_backend.service.Impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.repository.MenuItemRepository;
import com.siteshkumar.zomato_clone_backend.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final MenuItemRepository menuItemRepository;

    @Override
    @Transactional
    public void restoreStock(Long menuItemId, Integer quantity) {

        log.info("Restoring stock. MenuItemId: {}, Quantity: {}", menuItemId, quantity);

        MenuItemEntity item = menuItemRepository
                            .findById(menuItemId)
                            .orElseThrow(() -> {
                                log.error("Menu item not found while restoring stock. Id: {}", menuItemId);
                                return new ResourceNotFoundException("Item not found");
                            });

        int updatedStock = item.getStock() + quantity;
        item.setStock(updatedStock);

        menuItemRepository.save(item);

        log.info("Stock restored successfully. MenuItemId: {}, New Stock: {}", 
                    menuItemId, updatedStock);
    }

    @Override
    @Transactional
    public void deductStock(Long menuItemId, Integer quantity) {

        log.info("Deducting stock. MenuItemId: {}, Quantity: {}", menuItemId, quantity);

        MenuItemEntity item = menuItemRepository
                .findById(menuItemId)
                .orElseThrow(() -> {
                    log.error("Menu item not found while deducting stock. Id: {}", menuItemId);
                    return new ResourceNotFoundException("Menu item not found");
                });

        if(!item.isAvailable()){
            log.warn("Stock deduction failed - Item not available. MenuItemId: {}", menuItemId);
            throw new RuntimeException("Item not available");
        }

        if(item.getStock() < quantity){
            log.warn("Stock deduction failed - Insufficient stock. MenuItemId: {}, Available: {}, Requested: {}", 
                        menuItemId, item.getStock(), quantity);
            throw new IllegalStateException("Insufficient stock");
        }

        int updatedStock = item.getStock() - quantity;
        item.setStock(updatedStock);

        menuItemRepository.save(item);

        log.info("Stock deducted successfully. MenuItemId: {}, Remaining Stock: {}", 
                    menuItemId, updatedStock);
    }
}