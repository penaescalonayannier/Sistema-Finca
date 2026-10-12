package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.ItemSalida;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface ItemSalidaReadDataJPARepository extends JpaRepository<ItemSalida, UUID> {

    @EntityGraph(attributePaths = {"trabajador", "labor", "fincaProducto", "fincaProducto.producto"})
    List<ItemSalida> findBySalidaId(UUID salidaId);

    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "labor", "fincaProducto", "fincaProducto.producto"})
    List<ItemSalida> findByTrabajadorId(UUID trabajadorId);

    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "labor", "fincaProducto", "fincaProducto.producto"})
    List<ItemSalida> findByIdIn(List<UUID> itemSalidaIds);

    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "labor", "fincaProducto", "fincaProducto.producto"})
    List<ItemSalida> findBySalidaIdIn(List<UUID> salidaIds);

    @EntityGraph(attributePaths = {"salida", "salida.fincaProducto", "salida.fincaProducto.producto", "labor"})
    @Query("SELECT i FROM ItemSalida i JOIN i.salida s JOIN s.fincaProducto fp JOIN fp.producto p " +
           "WHERE s.fincaId = :fincaId AND s.activo = true AND s.destino = com.kynsoft.report.domain.dto.DestinoSalida.INSUMO " +
           "AND i.laborId IS NOT NULL AND i.cantidadActividad IS NOT NULL AND i.cantidadActividad > 0 " +
           "AND s.fecha >= :inicio AND s.fecha < :fin AND (LOWER(p.name) LIKE '%combustible%' OR LOWER(p.name) LIKE '%diesel%' " +
           "OR LOWER(p.name) LIKE '%gasolina%' OR LOWER(p.name) LIKE '%fuel%' OR LOWER(p.name) LIKE '%nafta%' OR LOWER(p.name) LIKE '%glp%')")
    List<ItemSalida> findConsumosCombustible(@Param("fincaId") UUID fincaId,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);
}
