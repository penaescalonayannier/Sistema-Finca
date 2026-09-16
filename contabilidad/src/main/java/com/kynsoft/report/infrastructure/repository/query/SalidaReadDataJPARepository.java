package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.TipoSalida;
import com.kynsoft.report.infrastructure.entity.Salida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface SalidaReadDataJPARepository extends JpaRepository<Salida, UUID>, JpaSpecificationExecutor<Salida> {

    @Override
    @EntityGraph(attributePaths = {"fincaProducto", "fincaProducto.finca", "fincaProducto.producto", "items", "items.trabajador", "items.fincaProducto", "items.fincaProducto.producto"})
    Page<Salida> findAll(Specification specification, Pageable pageable);

    @Query("SELECT s FROM Salida s " +
           "LEFT JOIN FETCH s.fincaProducto fp " +
           "LEFT JOIN FETCH fp.finca " +
           "LEFT JOIN FETCH fp.producto " +
           "LEFT JOIN FETCH s.items i " +
           "LEFT JOIN FETCH i.trabajador " +
           "LEFT JOIN FETCH i.fincaProducto ifp " +
           "LEFT JOIN FETCH ifp.producto " +
           "WHERE s.id = :id")
    Optional<Salida> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT DISTINCT s FROM Salida s " +
           "LEFT JOIN FETCH s.fincaProducto fp LEFT JOIN FETCH fp.finca LEFT JOIN FETCH fp.producto " +
           "LEFT JOIN FETCH s.items i LEFT JOIN FETCH i.trabajador " +
           "WHERE s.id IN :ids")
    List<Salida> findByIdInWithDetails(@Param("ids") List<UUID> ids);

    List<Salida> findByTipo(TipoSalida tipo);

    List<Salida> findByFincaProductoId(UUID fincaProductoId);

    @Query("SELECT DISTINCT s FROM Salida s " +
           "JOIN FETCH s.fincaProducto fp " +
           "JOIN FETCH fp.finca " +
           "JOIN FETCH fp.producto " +
           "LEFT JOIN FETCH s.items i " +
           "LEFT JOIN FETCH i.trabajador " +
           "LEFT JOIN FETCH i.fincaProducto ifp " +
           "LEFT JOIN FETCH ifp.producto " +
           "WHERE s.tipo = :tipo AND s.destino = :destino " +
           "AND s.fecha >= :fechaInicio AND s.fecha < :fechaFin AND s.activo = true " +
           "ORDER BY s.fecha, s.numero")
    List<Salida> findActivasPorTipoDestinoYFecha(
            @Param("tipo") TipoSalida tipo,
            @Param("destino") com.kynsoft.report.domain.dto.DestinoSalida destino,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT DISTINCT s FROM Salida s " +
           "JOIN FETCH s.fincaProducto fp " +
           "JOIN FETCH fp.finca " +
           "JOIN FETCH fp.producto " +
           "LEFT JOIN FETCH s.items i " +
           "LEFT JOIN FETCH i.trabajador " +
           "LEFT JOIN FETCH i.fincaProducto ifp " +
           "LEFT JOIN FETCH ifp.producto " +
           "WHERE s.tipo = :tipo AND s.fecha >= :fechaInicio AND s.fecha < :fechaFin AND s.activo = true " +
           "ORDER BY s.destino, s.fecha, s.numero")
    List<Salida> findActivasPorTipoYFecha(
            @Param("tipo") TipoSalida tipo,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(s.numero, LENGTH(:prefix) + 1) AS int)), 0) FROM Salida s WHERE s.numero LIKE :prefix%")
    Integer findMaxNumeroByPrefix(@Param("prefix") String prefix);

    @Query("SELECT s FROM Salida s " +
           "LEFT JOIN FETCH s.fincaProducto fp " +
           "LEFT JOIN FETCH fp.finca " +
           "LEFT JOIN FETCH fp.producto " +
           "LEFT JOIN FETCH s.items i " +
           "LEFT JOIN FETCH i.trabajador " +
           "LEFT JOIN FETCH i.fincaProducto ifp " +
           "LEFT JOIN FETCH ifp.producto " +
           "WHERE s.fecha >= :startDate AND s.fecha <= :endDate AND s.activo = true")
    List<Salida> findByFechaAndActivo(@Param("startDate") java.time.LocalDateTime startDate,
                                       @Param("endDate") java.time.LocalDateTime endDate);

    @Query("SELECT DISTINCT s FROM Salida s " +
           "LEFT JOIN FETCH s.fincaProducto fp " +
           "LEFT JOIN FETCH fp.producto " +
           "WHERE s.fecha BETWEEN :fechaInicio AND :fechaFin AND s.activo = true")
    List<Salida> findByFechaBetween(@Param("fechaInicio") java.time.LocalDateTime fechaInicio,
                                     @Param("fechaFin") java.time.LocalDateTime fechaFin);

    @Query("SELECT DISTINCT s FROM Salida s " +
           "LEFT JOIN FETCH s.fincaProducto fp " +
           "LEFT JOIN FETCH fp.finca f " +
           "LEFT JOIN FETCH fp.producto " +
           "LEFT JOIN FETCH s.items i " +
           "LEFT JOIN FETCH i.trabajador " +
           "LEFT JOIN FETCH i.fincaProducto ifp " +
           "LEFT JOIN FETCH ifp.producto " +
           "WHERE f.id = :fincaId AND s.fecha BETWEEN :fechaInicio AND :fechaFin AND s.activo = true")
    List<Salida> findByFincaIdAndFechaBetween(@Param("fincaId") UUID fincaId,
                                               @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
                                               @Param("fechaFin") java.time.LocalDateTime fechaFin);
}
