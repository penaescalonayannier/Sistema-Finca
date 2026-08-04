package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.DiaTrabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DiaTrabajoReadDataJPARepository 
    extends JpaRepository<DiaTrabajo, UUID>, JpaSpecificationExecutor<DiaTrabajo> {
    
    @Override
    Page<DiaTrabajo> findAll(Specification specification, Pageable pageable);
    
    List<DiaTrabajo> findByReporteIdOrderByFechaAsc(UUID reporteId);
    
    Optional<DiaTrabajo> findByReporteIdAndFecha(UUID reporteId, LocalDate fecha);
    
    // Este es el método que debe usarse para obtener días con trabajadores
    @Query("SELECT dt FROM DiaTrabajo dt " +
           "LEFT JOIN FETCH dt.reporte dr " +
           "LEFT JOIN FETCH dt.trabajadores td " +
           "LEFT JOIN FETCH td.trabajador t " +
           "WHERE dt.reporte.id = :reporteId " +
           "ORDER BY dt.fecha ASC")
    List<DiaTrabajo> findByReporteIdWithTrabajadores(@Param("reporteId") UUID reporteId);
    
    @Query("SELECT dt FROM DiaTrabajo dt " +
           "LEFT JOIN FETCH dt.reporte dr " +
           "LEFT JOIN FETCH dt.trabajadores td " +
           "LEFT JOIN FETCH td.trabajador t " +
           "WHERE dt.reporte.year = :year AND dt.reporte.mes = :mes " +
           "ORDER BY dt.fecha ASC")
    List<DiaTrabajo> findByYearAndMesWithTrabajadores(
        @Param("year") String year,
        @Param("mes") String mes
    );
}