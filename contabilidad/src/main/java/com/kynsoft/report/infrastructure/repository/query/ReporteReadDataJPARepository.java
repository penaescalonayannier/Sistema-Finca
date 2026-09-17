package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Reporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface ReporteReadDataJPARepository extends JpaRepository<Reporte, UUID>, JpaSpecificationExecutor<Reporte> {

    @Override
    @EntityGraph(attributePaths = {"tipoReporte", "tipoCultivo", "tipoAnimal"})
    Page<Reporte> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.year = :year AND r.mes = :mes AND r.activo = true")
    long countByYearAndMes(@Param("year") String year, @Param("mes") String mes);

    @Query("SELECT MAX(r.codigo) FROM Reporte r WHERE r.year = :year AND r.mes = :mes")
    String findMaxCodigoByYearAndMes(@Param("year") String year, @Param("mes") String mes);

    @Query("SELECT DISTINCT r FROM Reporte r " +
           "JOIN r.dias d " +
           "JOIN d.trabajadores td " +
           "WHERE td.trabajador.id = :trabajadorId " +
           "AND r.year = :year " +
           "AND r.mes = :mes " +
           "AND r.activo = true " +
           "ORDER BY r.fecha DESC")
    List<Reporte> findByTrabajadorIdAndYearAndMes(
            @Param("trabajadorId") UUID trabajadorId,
            @Param("year") String year,
            @Param("mes") String mes);
}
