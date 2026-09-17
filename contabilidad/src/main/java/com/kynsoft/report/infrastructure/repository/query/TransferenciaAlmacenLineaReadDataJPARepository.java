package com.kynsoft.report.infrastructure.repository.query;
import com.kynsoft.report.infrastructure.entity.TransferenciaAlmacenLinea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Transactional(readOnly = true, transactionManager = "readTransactionManager")
public interface TransferenciaAlmacenLineaReadDataJPARepository extends JpaRepository<TransferenciaAlmacenLinea, UUID> {
    List<TransferenciaAlmacenLinea> findByTransferenciaId(UUID transferenciaId);
}
