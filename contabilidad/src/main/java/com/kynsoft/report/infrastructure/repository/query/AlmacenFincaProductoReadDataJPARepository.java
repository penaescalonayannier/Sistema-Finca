package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.AlmacenFincaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface AlmacenFincaProductoReadDataJPARepository
        extends JpaRepository<AlmacenFincaProducto, UUID>, JpaSpecificationExecutor<AlmacenFincaProducto> {

    @EntityGraph(attributePaths = {"almacen", "fincaProducto", "fincaProducto.producto"})
    Optional<AlmacenFincaProducto> findById(UUID id);

    @EntityGraph(attributePaths = {"almacen", "fincaProducto", "fincaProducto.producto"})
    List<AlmacenFincaProducto> findByAlmacenIdAndActivoTrue(UUID almacenId);

    @EntityGraph(attributePaths = {"almacen", "fincaProducto", "fincaProducto.producto"})
    Page<AlmacenFincaProducto> findByAlmacenIdAndActivoTrue(UUID almacenId, Pageable pageable);

    @EntityGraph(attributePaths = {"almacen", "fincaProducto", "fincaProducto.producto"})
    List<AlmacenFincaProducto> findByFincaProductoIdAndActivoTrue(UUID fincaProductoId);

    Optional<AlmacenFincaProducto> findByAlmacenIdAndFincaProductoId(UUID almacenId, UUID fincaProductoId);

    Optional<AlmacenFincaProducto> findByAlmacenIdAndFincaProductoIdAndActivoTrue(UUID almacenId, UUID fincaProductoId);

    boolean existsByAlmacenIdAndFincaProductoIdAndActivoTrue(UUID almacenId, UUID fincaProductoId);

    @Query("SELECT SUM(afp.stock) FROM AlmacenFincaProducto afp WHERE afp.almacen.id = :almacenId AND afp.activo = true")
    Double sumStockByAlmacenId(@Param("almacenId") UUID almacenId);

    @Query("SELECT SUM(afp.stock) FROM AlmacenFincaProducto afp WHERE afp.fincaProducto.id = :fincaProductoId AND afp.activo = true")
    Double sumStockByFincaProductoId(@Param("fincaProductoId") UUID fincaProductoId);

    @Query("SELECT COUNT(afp) FROM AlmacenFincaProducto afp WHERE afp.almacen.id = :almacenId AND afp.activo = true")
    Long countByAlmacenIdAndActivoTrue(@Param("almacenId") UUID almacenId);

    @Query("SELECT afp FROM AlmacenFincaProducto afp " +
           "WHERE afp.almacen.finca.id = :fincaId AND afp.activo = true " +
           "ORDER BY afp.fincaProducto.producto.name")
    @EntityGraph(attributePaths = {"almacen", "fincaProducto", "fincaProducto.producto"})
    List<AlmacenFincaProducto> findByFincaIdAndActivoTrue(@Param("fincaId") UUID fincaId);

    @Query("SELECT afp FROM AlmacenFincaProducto afp " +
           "WHERE afp.almacen.id != :almacenId " +
           "AND afp.almacen.finca.id = :fincaId " +
           "AND afp.fincaProducto.id = :fincaProductoId " +
           "AND afp.almacen.activo = true " +
           "AND afp.activo = true")
    @EntityGraph(attributePaths = {"almacen"})
    List<AlmacenFincaProducto> findAlmacenesDestinoDisponibles(
            @Param("almacenId") UUID almacenId,
            @Param("fincaId") UUID fincaId,
            @Param("fincaProductoId") UUID fincaProductoId);
}
