package com.kynsoft.report.infrastructure.repository.query;
import com.kynsoft.report.infrastructure.entity.ChequeTransferencia; import org.springframework.data.jpa.repository.*; import org.springframework.stereotype.Repository; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Repository @Transactional(readOnly=true,transactionManager="readTransactionManager") public interface ChequeTransferenciaReadDataJPARepository extends JpaRepository<ChequeTransferencia,UUID> { List<ChequeTransferencia> findByFincaIdOrderByFechaEmisionDesc(UUID fincaId); }
