package com.siteshkumar.zomato_clone_backend.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.AddressEntity;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, Long>{
    
}
