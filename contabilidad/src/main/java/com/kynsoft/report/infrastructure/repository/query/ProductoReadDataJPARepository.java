package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductoReadDataJPARepository extends JpaRepository<Producto, UUID>, JpaSpecificationExecutor<Producto> {
    
    @Override
    Page<Producto> findAll(Specification specification, Pageable pageable);
    
    // Métodos adicionales útiles para consultas específicas
    Optional<Producto> findByCode(String code);
    
    List<Producto> findByActiveTrue();
    
    List<Producto> findByStockLessThan(Integer stock);
    
    @Query("SELECT p FROM Producto p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Producto> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}