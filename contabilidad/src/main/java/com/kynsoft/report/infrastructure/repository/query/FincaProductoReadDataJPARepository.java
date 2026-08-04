package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.FincaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FincaProductoReadDataJPARepository extends JpaRepository<FincaProducto, UUID>, JpaSpecificationExecutor<FincaProducto> {
    
    @Override
    Page<FincaProducto> findAll(Specification specification, Pageable pageable);

    List<FincaProducto> findByFincaId(UUID fincaId);
    
    List<FincaProducto> findByProductoId(UUID productoId);
    
    Optional<FincaProducto> findByFincaIdAndProductoId(UUID fincaId, UUID productoId);
    
    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.finca.id = :fincaId")
    List<FincaProducto> findWithDetailsByFincaId(@Param("fincaId") UUID fincaId);
    
    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.producto.id = :productoId")
    List<FincaProducto> findWithDetailsByProductoId(@Param("productoId") UUID productoId);
    
    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.finca.id = :fincaId AND fp.producto.id = :productoId")
    Optional<FincaProducto> findWithDetailsByFincaIdAndProductoId(
        @Param("fincaId") UUID fincaId, 
        @Param("productoId") UUID productoId
    );
}