package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.infrastructure.entity.IncidenciaArqueoCaja;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface IncidenciaArqueoCajaWriteDataJPARepository extends JpaRepository<IncidenciaArqueoCaja, UUID> { @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select i from IncidenciaArqueoCaja i where i.id=:id") Optional<IncidenciaArqueoCaja> findByIdForUpdate(@Param("id") UUID id); }
