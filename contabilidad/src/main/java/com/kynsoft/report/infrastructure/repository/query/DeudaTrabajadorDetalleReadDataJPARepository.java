package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.DeudaTrabajadorDetalle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface DeudaTrabajadorDetalleReadDataJPARepository extends JpaRepository<DeudaTrabajadorDetalle, UUID>, JpaSpecificationExecutor<DeudaTrabajadorDetalle> {

    @Query("SELECT d FROM DeudaTrabajadorDetalle d WHERE d.trabajadorId = :trabajadorId AND d.activo = true ORDER BY d.fecha DESC")
    List<DeudaTrabajadorDetalle> findByTrabajadorIdAndActivoTrue(@Param("trabajadorId") UUID trabajadorId);

    @Query("SELECT d FROM DeudaTrabajadorDetalle d WHERE d.trabajadorId = :trabajadorId AND d.activo = true ORDER BY d.fecha DESC")
    Page<DeudaTrabajadorDetalle> findByTrabajadorIdAndActivoTrue(@Param("trabajadorId") UUID trabajadorId, Pageable pageable);

    List<DeudaTrabajadorDetalle> findBySalidaIdAndActivoTrue(UUID salidaId);

    // Obtener compras no pagadas ordenadas por fecha (FIFO - más antiguas primero)
    @Query("SELECT d FROM DeudaTrabajadorDetalle d WHERE d.trabajadorId = :trabajadorId " +
           "AND d.activo = true AND d.pagado = false AND d.tipoMovimiento = 'COMPRA' " +
           "ORDER BY d.fecha ASC")
    List<DeudaTrabajadorDetalle> findComprasNoPagadasByTrabajadorId(@Param("trabajadorId") UUID trabajadorId);

    @Query("SELECT d FROM DeudaTrabajadorDetalle d " +
           "LEFT JOIN FETCH d.trabajador t " +
           "LEFT JOIN FETCH t.finca " +
           "WHERE d.activo = true AND d.tipoMovimiento = 'PAGO' " +
           "AND d.fecha >= :fechaInicio AND d.fecha <= :fechaFin " +
           "ORDER BY d.fecha ASC")
    List<DeudaTrabajadorDetalle> findPagosActivosByFechaBetween(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT d FROM DeudaTrabajadorDetalle d " +
           "LEFT JOIN FETCH d.trabajador t " +
           "LEFT JOIN FETCH t.finca " +
           "WHERE d.activo = true AND d.tipoMovimiento = 'PAGO' AND t.fincaId = :fincaId " +
           "AND d.fecha >= :fechaInicio AND d.fecha <= :fechaFin " +
           "ORDER BY d.fecha ASC")
    List<DeudaTrabajadorDetalle> findPagosActivosByFincaIdAndFechaBetween(
            @Param("fincaId") UUID fincaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}
