package com.siteshkumar.zomato_clone_backend.mapper;

import org.springframework.stereotype.Component;
import com.siteshkumar.zomato_clone_backend.dto.admin.UserApproveResponseDto;
import com.siteshkumar.zomato_clone_backend.entity.UserEntity;

@Component
public class UserMapper {
    public UserApproveResponseDto toResponseDto(UserEntity entity){

        return new UserApproveResponseDto(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getStatus().name()
        );
    }
}