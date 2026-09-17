package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.HombreActividadAgricolaImporte;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface HombreActividadAgricolaImporteReadDataJPARepository extends JpaRepository<HombreActividadAgricolaImporte, UUID>, JpaSpecificationExecutor<HombreActividadAgricolaImporte> {
    @Override
    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "trabajador.grupo", "trabajador.cargo", "trabajador.plaza", "labor", "instrumento", "bloque", "bloque.finca", "campo", "campo.bloque", "campo.bloque.finca", "campo.variedad", "campo.cepa"})
    java.util.Optional<HombreActividadAgricolaImporte> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = {"trabajador", "trabajador.finca", "trabajador.grupo", "trabajador.cargo", "trabajador.plaza", "labor", "instrumento", "bloque", "bloque.finca", "campo", "campo.bloque", "campo.bloque.finca", "campo.variedad", "campo.cepa"})
    Page<HombreActividadAgricolaImporte> findAll(Specification specification, Pageable pageable);
}
