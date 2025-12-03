package com.cop_3060.repository;

import com.cop_3060.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    
    /**
     * Find resources by category ID.
     */
    Page<Resource> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * Find resources by location ID.
     */
    Page<Resource> findByLocationId(Long locationId, Pageable pageable);

    /**
     * Search resources by name (case-insensitive).
     */
    @Query("SELECT r FROM Resource r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    Page<Resource> searchByName(String name, Pageable pageable);

    /**
     * Count resources by category ID.
     */
    int countByCategoryId(Long categoryId);

    /**
     * Count resources by location ID.
     */
    int countByLocationId(Long locationId);
}
