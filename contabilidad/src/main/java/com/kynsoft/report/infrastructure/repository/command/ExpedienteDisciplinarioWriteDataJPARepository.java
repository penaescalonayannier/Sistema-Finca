package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.ExpedienteDisciplinario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ExpedienteDisciplinarioWriteDataJPARepository extends JpaRepository<ExpedienteDisciplinario, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from ExpedienteDisciplinario e where e.id = :id")
    Optional<ExpedienteDisciplinario> findByIdForUpdate(@Param("id") UUID id);
}
