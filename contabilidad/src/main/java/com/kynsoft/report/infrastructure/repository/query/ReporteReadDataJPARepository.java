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

    /**
     * El mapeo de Reporte a DTO se ejecuta fuera de la sesión del repositorio de
     * lectura (CQRS usa un EntityManager independiente). Por eso cada relación
     * consumida por {@code Reporte.toAggregate()} debe quedar materializada aquí.
     */
    @Override
    @EntityGraph(attributePaths = {"tipoReporte", "tipoCultivo", "tipoAnimal", "trabajadorResponsable"})
    java.util.Optional<Reporte> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"tipoReporte", "tipoCultivo", "tipoAnimal", "trabajadorResponsable"})
    Page<Reporte> findAll(Specification specification, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.year = :year AND r.mes = :mes AND r.activo = true")
    long countByYearAndMes(@Param("year") String year, @Param("mes") String mes);

    @Query("SELECT MAX(r.codigo) FROM Reporte r WHERE r.year = :year AND r.mes = :mes")
    String findMaxCodigoByYearAndMes(@Param("year") String year, @Param("mes") String mes);

    @EntityGraph(attributePaths = {"tipoReporte", "tipoCultivo", "tipoAnimal", "trabajadorResponsable"})
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

    @Query(value = "SELECT r.tipo_cultivo_id, COALESCE(tc.nombre, 'Sin cultivo'), " +
            "COALESCE(SUM(r.cantidad_cosechada * r.precio_unitario_referencia), 0), " +
            "COALESCE(SUM(COALESCE(NULLIF(td.horas, ''), '0')::numeric * COALESCE(c.salario_escala, 0) / 192), 0) " +
            "FROM reporte r LEFT JOIN tipo_cultivo tc ON tc.id = r.tipo_cultivo_id " +
            "LEFT JOIN dia_trabajo dt ON dt.reporte_id = r.id " +
            "LEFT JOIN trabajador_dia td ON td.dia_trabajo_id = dt.id " +
            "LEFT JOIN trabajador t ON t.id = td.trabajador_id LEFT JOIN cargo c ON c.id = t.cargo_id " +
            "WHERE r.finca_id = :fincaId AND r.year = :year AND r.mes = :mes AND r.activo = true " +
            "AND r.cantidad_cosechada IS NOT NULL GROUP BY r.tipo_cultivo_id, tc.nombre", nativeQuery = true)
    List<Object[]> resumenCosechaPorCultivo(@Param("fincaId") UUID fincaId, @Param("year") String year, @Param("mes") String mes);
}
