package com.kynsoft.report.infrastructure.repository.command;
import com.kynsoft.report.domain.dto.TipoFondoCaja;
import com.kynsoft.report.infrastructure.entity.FondoCajaAutorizado;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface FondoCajaAutorizadoWriteDataJPARepository extends JpaRepository<FondoCajaAutorizado, UUID> { @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select f from FondoCajaAutorizado f where f.fincaId=:fincaId and f.tipo=:tipo") Optional<FondoCajaAutorizado> findForUpdate(@Param("fincaId") UUID fincaId, @Param("tipo") TipoFondoCaja tipo); }
