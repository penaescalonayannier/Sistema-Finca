package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.LiquidacionItemSalida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface LiquidacionItemSalidaReadDataJPARepository extends JpaRepository<LiquidacionItemSalida, UUID> {
    @Query("SELECT COALESCE(SUM(i.importe), 0) FROM LiquidacionItemSalida i " +
           "JOIN LiquidacionSalida l ON l.id = i.liquidacionSalidaId " +
           "WHERE i.itemSalidaId = :itemSalidaId AND l.activo = true")
    Double totalCobradoByItemSalidaId(@Param("itemSalidaId") UUID itemSalidaId);

    @Query("SELECT i FROM LiquidacionItemSalida i JOIN LiquidacionSalida l ON l.id = i.liquidacionSalidaId " +
           "WHERE i.itemSalidaId IN :itemSalidaIds AND l.activo = true")
    List<LiquidacionItemSalida> findActivasByItemSalidaIdIn(@Param("itemSalidaIds") List<UUID> itemSalidaIds);

    @Query("SELECT i FROM LiquidacionItemSalida i JOIN LiquidacionSalida l ON l.id = i.liquidacionSalidaId " +
           "WHERE l.activo = true AND l.fecha >= :fechaInicio AND l.fecha <= :fechaFin " +
           "AND (:fincaId IS NULL OR l.fincaId = :fincaId) ORDER BY l.fecha ASC")
    List<LiquidacionItemSalida> findActivasByFincaIdAndFechaBetween(@Param("fincaId") UUID fincaId,
                                                                      @Param("fechaInicio") LocalDateTime fechaInicio,
                                                                      @Param("fechaFin") LocalDateTime fechaFin);
}
