package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.ArqueoCaja;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArqueoCajaWriteDataJPARepository extends JpaRepository<ArqueoCaja, UUID> {
    @Query(value = "SELECT nextval('arqueo_caja_numero_seq')", nativeQuery = true)
    Long siguienteNumero();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ArqueoCaja a WHERE a.id = :id")
    Optional<ArqueoCaja> findByIdForUpdate(@Param("id") UUID id);
}
