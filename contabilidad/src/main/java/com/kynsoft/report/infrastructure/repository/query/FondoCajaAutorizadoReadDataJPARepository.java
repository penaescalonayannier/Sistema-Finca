package com.kynsoft.report.infrastructure.repository.query;
import com.kynsoft.report.infrastructure.entity.FondoCajaAutorizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Transactional(readOnly=true, transactionManager="readTransactionManager") public interface FondoCajaAutorizadoReadDataJPARepository extends JpaRepository<FondoCajaAutorizado, UUID> { List<FondoCajaAutorizado> findByFincaIdOrderByTipo(UUID fincaId); }
