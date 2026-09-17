package com.kynsoft.report.infrastructure.repository.query;
import com.kynsoft.report.infrastructure.entity.ConteoFisicoLinea; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Transactional(readOnly=true, transactionManager="readTransactionManager") public interface ConteoFisicoLineaReadDataJPARepository extends JpaRepository<ConteoFisicoLinea, UUID> { List<ConteoFisicoLinea> findByConteoIdOrderByProductoNombreAsc(UUID conteoId); }
