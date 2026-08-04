package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.TrabajadorReporte;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TrabajadorReporteReadDataJPARepository extends JpaRepository<TrabajadorReporte, UUID>, JpaSpecificationExecutor<TrabajadorReporte> {
    
    @Override
    Page<TrabajadorReporte> findAll(Specification specification, Pageable pageable);

    Optional<TrabajadorReporte> findByTrabajadorIdAndReporteId(UUID trabajadorId, UUID reporteId);

    // NUEVO MÉTODO: Obtener todas las asignaciones de un reporte con JOIN FETCH para evitar N+1
    @Query("SELECT tr FROM TrabajadorReporte tr " +
           "JOIN FETCH tr.trabajador t " +
           "JOIN FETCH tr.reporte r " +
           "WHERE tr.reporte.id = :reporteId")
    List<TrabajadorReporte> findByReporteId(@Param("reporteId") UUID reporteId);
    
    // NUEVO MÉTODO: Obtener todas las asignaciones de un trabajador con JOIN FETCH
    @Query("SELECT tr FROM TrabajadorReporte tr " +
           "JOIN FETCH tr.trabajador t " +
           "JOIN FETCH tr.reporte r " +
           "WHERE tr.trabajador.id = :trabajadorId")
    List<TrabajadorReporte> findByTrabajadorId(@Param("trabajadorId") UUID trabajadorId);

        // NUEVO MÉTODO: Obtener asignaciones por año y mes
    @Query("SELECT tr FROM TrabajadorReporte tr " +
           "JOIN FETCH tr.trabajador t " +
           "JOIN FETCH tr.reporte r " +
           "WHERE r.year = :year AND r.mes = :mes")
    List<TrabajadorReporte> findByReporteYearAndReporteMes(
        @Param("year") String year, 
        @Param("mes") String mes
    );
}