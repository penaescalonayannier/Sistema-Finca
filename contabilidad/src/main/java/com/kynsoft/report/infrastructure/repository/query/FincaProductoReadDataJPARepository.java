package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.FincaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface FincaProductoReadDataJPARepository extends JpaRepository<FincaProducto, UUID>, JpaSpecificationExecutor<FincaProducto> {

    /**
     * Las lecturas se consumen desde el lado CQRS de consulta, fuera de su
     * EntityManager. Materializar estas dos relaciones evita que los listados
     * y las alertas fallen al construir los DTO de inventario.
     */
    @Override
    @EntityGraph(attributePaths = {"finca", "producto"})
    Optional<FincaProducto> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"finca", "producto"})
    List<FincaProducto> findAll();

    @Override
    @EntityGraph(attributePaths = {"finca", "producto"})
    List<FincaProducto> findAllById(Iterable<UUID> ids);

    @Override
    @EntityGraph(attributePaths = {"finca", "producto"})
    Page<FincaProducto> findAll(Specification specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"finca", "producto"})
    List<FincaProducto> findAll(Specification specification);

    @EntityGraph(attributePaths = {"finca", "producto"})
    List<FincaProducto> findByFincaIdAndActivoTrue(UUID fincaId);

    @EntityGraph(attributePaths = {"finca", "producto"})
    List<FincaProducto> findByProductoIdAndActivoTrue(UUID productoId);

    Long countByFincaIdAndActivoTrue(UUID fincaId);

    @EntityGraph(attributePaths = {"finca", "producto"})
    Optional<FincaProducto> findByFincaIdAndProductoIdAndActivoTrue(UUID fincaId, UUID productoId);

    // Mantener para uso interno (incluye inactivos)
    @EntityGraph(attributePaths = {"finca", "producto"})
    List<FincaProducto> findByFincaId(UUID fincaId);

    @EntityGraph(attributePaths = {"finca", "producto"})
    Optional<FincaProducto> findByFincaIdAndProductoId(UUID fincaId, UUID productoId);

    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.finca.id = :fincaId AND fp.activo = true")
    List<FincaProducto> findWithDetailsByFincaId(@Param("fincaId") UUID fincaId);

    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.producto.id = :productoId AND fp.activo = true")
    List<FincaProducto> findWithDetailsByProductoId(@Param("productoId") UUID productoId);

    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.finca.id = :fincaId AND fp.producto.id = :productoId AND fp.activo = true")
    Optional<FincaProducto> findWithDetailsByFincaIdAndProductoId(
        @Param("fincaId") UUID fincaId,
        @Param("productoId") UUID productoId
    );

    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.id = :id")
    Optional<FincaProducto> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT fp FROM FincaProducto fp " +
           "JOIN FETCH fp.finca f " +
           "JOIN FETCH fp.producto p " +
           "WHERE fp.id IN :ids")
    List<FincaProducto> findByIdInWithDetails(@Param("ids") List<UUID> ids);
}
