package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.TipoAccion;
import com.kynsoft.report.infrastructure.entity.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface AuditoriaReadDataJPARepository extends JpaRepository<Auditoria, UUID>, JpaSpecificationExecutor<Auditoria> {

    @Override
    Page<Auditoria> findAll(Specification<Auditoria> specification, Pageable pageable);

    List<Auditoria> findByUsuarioIdOrderByCreatedAtDesc(UUID usuarioId);

    List<Auditoria> findByEntidadAndEntidadIdOrderByCreatedAtDesc(String entidad, UUID entidadId);

    Page<Auditoria> findByAccionOrderByCreatedAtDesc(TipoAccion accion, Pageable pageable);

    Page<Auditoria> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT DISTINCT a.entidad FROM Auditoria a ORDER BY a.entidad")
    List<String> findDistinctEntidades();
}
