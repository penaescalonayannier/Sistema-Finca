package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.MovimientoDepreciacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovimientoDepreciacionReadRepository extends JpaRepository<MovimientoDepreciacion, UUID> {

    List<MovimientoDepreciacion> findByActivoFijoIdOrderByAnioDescMesDesc(UUID activoFijoId);

    List<MovimientoDepreciacion> findByMesAndAnioOrderByActivoFijoNumeroInventarioAsc(int mes, int anio);

    boolean existsByMesAndAnio(int mes, int anio);

    @Query("SELECT m FROM MovimientoDepreciacion m WHERE m.anio = :anio ORDER BY m.activoFijo.numeroInventario, m.mes")
    List<MovimientoDepreciacion> findByAnio(@Param("anio") int anio);

    @Query("SELECT m FROM MovimientoDepreciacion m " +
           "WHERE m.activoFijo.id = :activoFijoId AND m.mes = :mes AND m.anio = :anio")
    List<MovimientoDepreciacion> findByActivoFijoIdAndPeriodo(@Param("activoFijoId") UUID activoFijoId,
                                                               @Param("mes") int mes,
                                                               @Param("anio") int anio);
}
