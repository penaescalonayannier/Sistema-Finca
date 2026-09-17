package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.TransferenciaAlmacen;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;
public interface TransferenciaAlmacenWriteDataJPARepository extends JpaRepository<TransferenciaAlmacen, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TransferenciaAlmacen t where t.id = :id")
    Optional<TransferenciaAlmacen> findByIdForUpdate(UUID id);
}
