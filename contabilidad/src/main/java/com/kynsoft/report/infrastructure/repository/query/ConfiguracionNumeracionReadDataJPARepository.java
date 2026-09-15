package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.domain.dto.TipoDocumento;
import com.kynsoft.report.infrastructure.entity.ConfiguracionNumeracion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface ConfiguracionNumeracionReadDataJPARepository extends JpaRepository<ConfiguracionNumeracion, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConfiguracionNumeracion c WHERE c.fincaId = :fincaId AND c.tipo = :tipo AND c.anio = :anio")
    Optional<ConfiguracionNumeracion> findByFincaIdAndTipoAndAnioForUpdate(
            @Param("fincaId") UUID fincaId,
            @Param("tipo") TipoDocumento tipo,
            @Param("anio") Integer anio
    );

    Optional<ConfiguracionNumeracion> findByFincaIdAndTipoAndAnio(UUID fincaId, TipoDocumento tipo, Integer anio);
}
