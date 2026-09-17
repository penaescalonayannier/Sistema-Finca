package com.kynsoft.report.infrastructure.repository.query;

import com.kynsoft.report.infrastructure.entity.Plaza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface PlazaReadDataJPARepository extends JpaRepository<Plaza, UUID> {
    List<Plaza> findByFincaIdOrderByCodigo(UUID fincaId);
    boolean existsByFincaIdAndCodigo(UUID fincaId, String codigo);
}
