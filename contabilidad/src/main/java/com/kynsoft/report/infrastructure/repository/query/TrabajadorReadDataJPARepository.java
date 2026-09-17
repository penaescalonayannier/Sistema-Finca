package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Trabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface TrabajadorReadDataJPARepository extends JpaRepository<Trabajador, UUID>, JpaSpecificationExecutor<Trabajador> {
    /** Los reportes de trabajo muestran el cargo después de la lectura CQRS. */
    @Override
    @EntityGraph(attributePaths = {"cargo"})
    java.util.List<Trabajador> findAll();

    @Override
    @EntityGraph(attributePaths = {"cargo"})
    java.util.List<Trabajador> findAll(Specification<Trabajador> specification);

    @Override
    @EntityGraph(attributePaths = {"cargo"})
    Page<Trabajador> findAll(Specification specification, Pageable pageable);

    Optional<Trabajador> findByRuc(String ruc);

    Optional<Trabajador> findByPlazaIdAndActivoTrue(UUID plazaId);

    Long countByFincaIdAndActivoTrue(UUID fincaId);

    Page<Trabajador> findByFincaIdAndActivoTrue(UUID fincaId, Pageable pageable);

    @Query("SELECT t FROM Trabajador t LEFT JOIN FETCH t.finca LEFT JOIN FETCH t.grupo LEFT JOIN FETCH t.cargo LEFT JOIN FETCH t.plaza WHERE t.id = :id")
    Optional<Trabajador> findByIdWithRelations(@Param("id") UUID id);

    @Query("SELECT t FROM Trabajador t LEFT JOIN FETCH t.finca LEFT JOIN FETCH t.grupo LEFT JOIN FETCH t.cargo LEFT JOIN FETCH t.plaza WHERE t.activo = true ORDER BY t.nombre")
    java.util.List<Trabajador> findAllActivosWithRelations();

    @Query("SELECT t FROM Trabajador t LEFT JOIN FETCH t.finca LEFT JOIN FETCH t.grupo LEFT JOIN FETCH t.cargo LEFT JOIN FETCH t.plaza WHERE t.activo = true AND t.fincaId = :fincaId ORDER BY t.nombre")
    java.util.List<Trabajador> findAllActivosWithRelationsByFincaId(@Param("fincaId") UUID fincaId);
}
