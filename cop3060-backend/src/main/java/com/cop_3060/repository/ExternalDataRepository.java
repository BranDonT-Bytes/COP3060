package com.cop_3060.repository;

import com.cop_3060.entity.ExternalData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExternalDataRepository extends JpaRepository<ExternalData, Long> {
    Optional<ExternalData> findFirstBySourceAndKeyNameOrderByFetchedAtDesc(String source, String keyName);
}
