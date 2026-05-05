package com.siteshkumar.zomato_clone_backend.service.Impl;

import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.siteshkumar.zomato_clone_backend.dto.address.AddressResponseDto;
import com.siteshkumar.zomato_clone_backend.dto.address.CreateAddressRequestDto;
import com.siteshkumar.zomato_clone_backend.entity.AddressDetails;
import com.siteshkumar.zomato_clone_backend.entity.AddressEntity;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;
import com.siteshkumar.zomato_clone_backend.exception.ResourceNotFoundException;
import com.siteshkumar.zomato_clone_backend.mapper.AddressMapper;
import com.siteshkumar.zomato_clone_backend.repository.mysql.AddressRepository;
import com.siteshkumar.zomato_clone_backend.service.AddressService;
import com.siteshkumar.zomato_clone_backend.utils.AuthUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public AddressResponseDto createAddress(CreateAddressRequestDto request) {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        AddressDetails details = new AddressDetails();
        details.setLabel(request.getLabel());
        details.setStreet(request.getStreet());
        details.setArea(request.getArea());
        details.setCity(request.getCity());
        details.setState(request.getState());
        details.setPincode(request.getPincode());

        AddressEntity address = new AddressEntity();
        address.setAddressDetails(details);
        address.setUser(user);

        AddressEntity savedAddress = addressRepository.save(address);

        return addressMapper.toResponseDto(savedAddress);
    }

    @Override
    public List<AddressResponseDto> getMyAddresses() {

        UserEntity user = authUtils.getCurrentLoggedInUser().getUser();

        return addressRepository.findByUserId(user.getId())
                .stream()
                .map(addressMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {

        UserEntity currentUser = authUtils.getCurrentLoggedInUser().getUser();

        AddressEntity address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this address");
        }

        addressRepository.delete(address);
    }
}