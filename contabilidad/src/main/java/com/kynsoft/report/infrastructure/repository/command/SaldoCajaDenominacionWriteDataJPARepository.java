package com.kynsoft.report.infrastructure.repository.command;

import com.kynsoft.report.infrastructure.entity.SaldoCajaDenominacion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaldoCajaDenominacionWriteDataJPARepository extends JpaRepository<SaldoCajaDenominacion, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SaldoCajaDenominacion s WHERE s.fincaId = :fincaId AND s.denominacion = :denominacion")
    Optional<SaldoCajaDenominacion> findByFincaIdAndDenominacionForUpdate(@Param("fincaId") UUID fincaId,
                                                                            @Param("denominacion") Integer denominacion);
}
