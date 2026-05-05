package com.siteshkumar.zomato_clone_backend.service;

import java.util.List;
import com.siteshkumar.zomato_clone_backend.dto.address.AddressResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.address.CreateAddressRequestDto;

public interface AddressService {

    AddressResponseDto createAddress(CreateAddressRequestDto request);
    List<AddressResponseDto> getMyAddresses();
    void deleteAddress(Long addressId);
}