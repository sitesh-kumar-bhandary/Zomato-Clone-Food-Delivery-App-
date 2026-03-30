package com.siteshkumar.zomato_clone_backend.mapper;

import org.springframework.stereotype.Component;

import com.siteshkumar.zomato_clone_backend.document.MenuItemDocument;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.CreateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.MenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.menuItem.UpdateMenuItemResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.MenuItemEntity;

@Component
public class MenuItemMapper {
    public CreateMenuItemResponseDto toCreateResponseDto (MenuItemEntity entity){

        return new CreateMenuItemResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.isAvailable()
        );
    }

    public UpdateMenuItemResponseDto toUpdateResponeDto(MenuItemEntity entity){

        return new UpdateMenuItemResponseDto(
                entity.getId(),
                entity.getRestaurant().getId(),
                entity.getName(),
                entity.getPrice(),
                entity.isAvailable()
        );
    }

    public MenuItemResponseDto toResponseDto (MenuItemEntity entity){

        return new MenuItemResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getRestaurant().getName()
        );
    }

    public MenuItemDocument toDocument(MenuItemEntity entity) {
        MenuItemDocument doc = new MenuItemDocument();
        doc.setId(entity.getId().toString());
        doc.setName(entity.getName());
        doc.setPrice(entity.getPrice().doubleValue());
        doc.setAvailable(entity.isAvailable());
        doc.setStock(entity.getStock());
        doc.setRestaurantId(entity.getRestaurant().getId().toString());

        return doc;
    }
    
}
