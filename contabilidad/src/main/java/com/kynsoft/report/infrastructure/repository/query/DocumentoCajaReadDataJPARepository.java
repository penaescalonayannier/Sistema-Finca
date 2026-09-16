package com.kynsoft.report.infrastructure.repository.query;
import com.kynsoft.report.infrastructure.entity.DocumentoCaja; import org.springframework.data.jpa.repository.*; import org.springframework.stereotype.Repository; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Repository @Transactional(readOnly=true,transactionManager="readTransactionManager") public interface DocumentoCajaReadDataJPARepository extends JpaRepository<DocumentoCaja,UUID> { List<DocumentoCaja> findByFincaIdOrderByFechaDesc(UUID fincaId); }
