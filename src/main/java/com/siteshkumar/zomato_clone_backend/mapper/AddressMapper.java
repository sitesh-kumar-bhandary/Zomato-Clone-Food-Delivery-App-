package com.siteshkumar.zomato_clone_backend.mapper;

import org.springframework.stereotype.Component;

import com.siteshkumar.zomato_clone_backend.dto.address.AddressResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.AddressEntity;

@Component
public class AddressMapper {

    public AddressResponseDto toResponseDto(AddressEntity entity) {

        AddressResponseDto dto = new AddressResponseDto();
        dto.setId(entity.getId());
        dto.setLabel(entity.getAddressDetails().getLabel());
        dto.setStreet(entity.getAddressDetails().getStreet());
        dto.setArea(entity.getAddressDetails().getArea());
        dto.setCity(entity.getAddressDetails().getCity());
        dto.setState(entity.getAddressDetails().getState());
        dto.setPincode(entity.getAddressDetails().getPincode());

        return dto;
    }
}