package com.siteshkumar.zomato_clone_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.siteshkumar.zomato_clone_backend.entity.AuditLogsEntity;

@Repository
public interface AuditLogsRepository extends JpaRepository<AuditLogsEntity, Long>{
    
}
