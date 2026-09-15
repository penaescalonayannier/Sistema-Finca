package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.TrabajadorDia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface TrabajadorDiaReadDataJPARepository 
    extends JpaRepository<TrabajadorDia, UUID>, JpaSpecificationExecutor<TrabajadorDia> {
    
    @Override
    Page<TrabajadorDia> findAll(Specification specification, Pageable pageable);
    
    List<TrabajadorDia> findByDiaTrabajoId(UUID diaTrabajoId);
    
    Optional<TrabajadorDia> findByDiaTrabajoIdAndTrabajadorId(UUID diaTrabajoId, UUID trabajadorId);
    
    @Query("SELECT td FROM TrabajadorDia td " +
           "JOIN FETCH td.trabajador t " +
           "WHERE td.diaTrabajo.id = :diaTrabajoId")
    List<TrabajadorDia> findByDiaTrabajoIdWithDetails(@Param("diaTrabajoId") UUID diaTrabajoId);

    @Query("SELECT td FROM TrabajadorDia td " +
           "JOIN td.diaTrabajo dt " +
           "JOIN dt.reporte r " +
           "WHERE td.trabajador.id = :trabajadorId " +
           "AND r.year = :year " +
           "AND r.mes = :mes")
    List<TrabajadorDia> findByTrabajadorIdAndYearAndMes(
            @Param("trabajadorId") UUID trabajadorId,
            @Param("year") String year,
            @Param("mes") String mes);
}